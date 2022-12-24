/**
 *
 *	Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.
 *	Copyright (C) 2022  JumperBot_
 *
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
**/
use std::env::consts::OS;
use std::fs::File;
use std::io::{BufRead, BufReader, Write};
use std::str::Chars;

use crate::command::Command;
use crate::memory_map::MemoryMap;
use crate::universal::Universal;

pub struct UFBC {
    pub file_name: String,
}

impl UFBC {
    pub fn compile(&self) {
        let default_memory_map: MemoryMap = MemoryMap {
            keys: vec![
                " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9", "\n",
            ]
            .into_iter()
            .map(|x| x.to_string())
            .collect::<Vec<String>>(),
            mems: (0..38).collect::<Vec<u64>>(),
        };
        let binary_map: MemoryMap = MemoryMap {
            keys: vec![
                "wvar", "nvar", "trim", "add", "sub", "mul", "div", "mod", "rmod", "nop", "jm",
                "jl", "je", "jne", "print", "read", "wfile", "rfile", "dfile",
            ]
            .into_iter()
            .map(|x| x.to_string())
            .collect::<Vec<String>>(),
            mems: (0..19).collect::<Vec<u64>>(),
        };
        let mut reader: BufReader<File> = match File::open(&self.file_name) {
            Ok(x) => BufReader::<File>::new(x),
            Err(x) => {
                Universal::err_exit(format!(
                    "File Provided Does Not Exist...\n{}\nTerminating...",
                    x.to_string(),
                ));
                return;
            }
        };
        let mut warnings: Vec<String> = Vec::<String>::new();
        let mut errors: Vec<String> = Vec::<String>::new();
        let mut compiled: Vec<u8> = Vec::<u8>::new();
        let mut memory_map: MemoryMap = MemoryMap::new();
        let mut buffer: String = String::new();
        let mut multiline_comment: bool = false;
        let mut line_number: usize = 1;
        let mut command_number: usize = 0;
        while reader.read_line(&mut buffer).unwrap() != 0 {
            buffer = buffer.trim().to_string();
            if !multiline_comment {
                if let Some(x) = buffer.find("/*") {
                    let s1: String = buffer[..x].to_string();
                    let s2: String = buffer[x + 2..].to_string();
                    if let Some(y) = s2.find("*/") {
                        buffer.clear();
                        buffer.push_str(&s1);
                        buffer.push_str(&s2[y + 2..]);
                        buffer = Self::extract_useful_from_line(&buffer);
                    } else {
                        buffer = s1.to_string();
                        multiline_comment = true;
                    }
                } else {
                    buffer = Self::extract_useful_from_line(&buffer);
                }
            } else {
                if let Some(x) = buffer.find("*/") {
                    buffer = buffer[x + 2..].to_string();
                    buffer = Self::extract_useful_from_line(&buffer);
                } else {
                    buffer.clear();
                }
            }
            buffer = buffer.trim().to_string();
            if !buffer.is_empty() {
                let real_line: Vec<String> = Self::split_line(&buffer);
                let line: Vec<String> = Self::substitute_strings_and_labels(
                    &real_line,
                    &mut memory_map,
                    &default_memory_map,
                );
                let command: String = real_line[0].clone().to_lowercase();
                if !command.eq("label") {
                    if line.len() < 2 && !command.eq("nop") {
                        warnings.push(format!("Warning(s) Found On Line {line_number}:"));
                        warnings.push(format!(
                            "Warning{}",
                            &Universal::format_error(
                                &line,
                                &[
                                    "Command",
                                    &real_line[0],
                                    "Will Be Ignored For It Has No Arguments",
                                ],
                            )[5..]
                        ));
                    } else {
                        match Command::new(&line, &real_line, &binary_map) {
                            Err(x) => {
                                errors.push(format!("Error(s) Found On Line {line_number} / Command Number {command_number}:"));
                                errors.push(x);
                            }
                            Ok(x) => {
                                for y in x {
                                    compiled.push(y);
                                }
                            }
                        }
                        command_number += 1;
                    }
                }
            }
            buffer.clear();
            line_number += 1;
        }
        if !warnings.is_empty() {
            if !OS.contains("windows") {
                print!("\u{001B}[93m");
            }
            println!("{}", {
                let mut temp: String = String::new();
                for x in warnings {
                    temp.push_str(&x);
                    temp.push('\n');
                }
                temp
            });
            if !OS.contains("windows") {
                print!("\n\u{001B}[0m");
            }
        }
        if !errors.is_empty() {
            Universal::err_exit({
                let mut temp: String = String::new();
                for x in errors {
                    temp.push_str(&x);
                    temp.push('\n');
                }
                temp
            });
        }
        match File::create(format!("{}b", &self.file_name)) {
            Ok(mut x) => {
                if let Err(y) = x.write_all(&compiled) {
                    Universal::err_exit(y.to_string());
                }
            }
            Err(x) => Universal::err_exit(x.to_string()),
        };
    }

    fn substitute_strings_and_labels(
        real_line: &Vec<String>,
        labels: &mut MemoryMap,
        default_memory_map: &MemoryMap,
    ) -> Vec<String> {
        if real_line[0].to_lowercase().eq("label") {
            Self::assign_labels(&real_line, labels);
            return Vec::<String>::new();
        }
        return Self::substitute_strings(&real_line, &labels, &default_memory_map);
    }
    fn substitute_strings(
        real_line: &Vec<String>,
        labels: &MemoryMap,
        default_memory_map: &MemoryMap,
    ) -> Vec<String> {
        let mut out: Vec<String> = Vec::<String>::new();
        for x in real_line.clone() {
            if x.starts_with("\"") && x.ends_with("\"") {
                let temp: String = x[1..x.len() - 1].to_string();
                for x2 in Universal::convert_to_mem(&temp, true, &labels, &default_memory_map) {
                    out.push(x2);
                }
            } else if x.starts_with("${") && x.ends_with("}") {
                let key: String = x[2..x.len() - 1].to_string();
                if labels.contains_key(&key) {
                    out.push(labels.get(&key).to_string());
                } else {
                    Universal::err_exit(Universal::format_error(
                        &real_line,
                        &[
                            "Memory Index Label Already Replaced By Another",
                            &real_line[1],
                            "Should Be Replaced With The Appropriate Label",
                        ],
                    ));
                }
            } else {
                out.push(x);
            }
        }
        return out;
    }
    fn assign_labels(real_line: &Vec<String>, labels: &mut MemoryMap) {
        if real_line.len() != 3 {
            Universal::err_exit(Universal::format_error(
                &real_line,
                &[
                    "Command",
                    "label",
                    "Needs No Less And No More Than Two Arguments To Work",
                ],
            ));
        }
        let label_mem_ind: u64 = match real_line[1].parse::<u64>() {
            Ok(x) => x,
            Err(_x) => {
                Universal::err_exit(Universal::format_error(
                    &real_line,
                    &[
                        "Memory Index Expected Instead Of",
                        &real_line[1],
                        "Should Be Replaced With A Memory Index",
                    ],
                ));
                0
            }
        };
        if label_mem_ind > 255 {
            Universal::err_exit(Universal::format_error(
                &real_line,
                &[
                    "Memory Index",
                    &real_line[1],
                    "Is Larger Than 255 And Will Not Point To Memory",
                ],
            ));
        }
        let label: String = real_line[2].replace("[${}]", "");
        labels.remove_mem_if_exists(&label_mem_ind);
        labels.put(&label, &label_mem_ind);
    }

    fn split_line(line: &str) -> Vec<String> {
        let mut out: Vec<String> = Vec::<String>::new();
        let mut buf: String = String::new();
        for x in line.to_string().chars() {
            if "[-|, \t]".contains(x.clone()) {
                if !buf.is_empty() {
                    out.push(buf);
                    buf = String::new();
                }
            } else {
                buf.push(x);
            }
        }
        if !buf.is_empty() {
            out.push(buf);
        }
        return out;
    }

    fn extract_useful_from_line(code: &str) -> String {
        let mut out: String = String::new();
        let x: String = Self::convert_dividers_in_string(&code).replace("[-|,\t]", " ");
        if let Some(y) = x.find("//") {
            out.push('\n');
            out.push_str(&x[..y]);
        } else {
            out.push('\n');
            out.push_str(&x);
        }
        return out;
    }

    fn convert_dividers_in_string(line: &str) -> String {
        // https://stackoverflow.com/a/70877609/16915219
        let chars: Chars = line.chars();
        let char_count: usize = chars.clone().count();
        if let Some(x) = chars.rev().position(|c| c == '\"') {
            let first_index: usize = line.find("\"").unwrap();
            let last_index: usize = char_count - x - 1;
            return format!(
                "{}{}{}",
                &line[..first_index],
                Self::escape_dividers_in_string(line[first_index..last_index].to_string()),
                &line[last_index..]
            )
            .to_string();
        }
        return line.to_string();
    }
    fn escape_dividers_in_string(input: String) -> String {
        let mut res: String = String::new();
        for x in input.chars() {
            if "-|, \t".contains(x.clone()) {
                res.push_str(&Self::escape_divider(x));
            } else {
                res.push(x);
            }
        }
        return res;
    }
    fn escape_divider(divider: char) -> String {
        return (match divider {
            '-' => "UU0045",
            '|' => "UU0124",
            ',' => "UU0044",
            ' ' => "UU0032",
            '\t' => "UU0009",
            _ => "UU0000",
        })
        .to_string();
    }
}
