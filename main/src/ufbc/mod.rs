mod command;

use std::collections::HashMap;
use std::env::consts::OS;
use std::fs::{self, File};
use std::io::{BufRead, BufReader, BufWriter, Write};
use std::str::Chars;

use crate::memory_map::MemoryMap;
use crate::Universal;
use command::Command;

struct LineExtractionResult {
    multiline_comment: bool,
    res: String,
}

#[allow(clippy::upper_case_acronyms)]
pub struct UFBC {
    pub file_name: String,
}

impl UFBC {
    pub fn compile(&self) {
        let mut reader: BufReader<File> = match File::open(&self.file_name) {
            Ok(x) => BufReader::<File>::new(x),
            Err(x) => return Universal::err_exit(x.to_string()),
        };
        let mut writer: BufWriter<File> = match File::create(format!("{}b", &self.file_name)) {
            Ok(x) => BufWriter::<File>::with_capacity(300, x),
            Err(x) => return Universal::err_exit(x.to_string()),
        };
        let (mut warnings, mut errors): (Vec<String>, Vec<String>) = (vec![], vec![]);
        let (mut multiline_comment, mut stop_compilling_file): (bool, bool) = (false, false);
        let (mut line_number, mut command_number): (usize, usize) = (1, 0);
        let mut labels: HashMap<String, u8> = HashMap::<String, u8>::new();
        let mut buffer: String = String::new();
        let (default_memory_map, binary_map): (MemoryMap, MemoryMap) =
            (MemoryMap::new_limited(), MemoryMap::new_binary_map());
        while reader.read_line(&mut buffer).unwrap() != 0 {
            let extracted: LineExtractionResult =
                Self::extract_useful_from_line(multiline_comment, buffer.trim());
            buffer = extracted.res;
            multiline_comment = extracted.multiline_comment;
            if !buffer.is_empty() {
                let real_line: Vec<String> = Self::split_line(&buffer);
                let line: Vec<String> = Self::substitute_strings_and_labels(
                    &real_line,
                    &mut labels,
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
                            #![allow(unused_must_use)]
                            Err(x) => {
                                if !stop_compilling_file {
                                    writer.flush();
                                    fs::remove_file(format!("{}b", self.file_name));
                                    stop_compilling_file = true;
                                }
                                errors.push(format!("Error(s) Found On Line {line_number} / Command Number {command_number}:"));
                                errors.push(x);
                            }
                            Ok(x) => {
                                if !stop_compilling_file {
                                    if let Err(x) = writer.write_all(&x) {
                                        writer.flush();
                                        fs::remove_file(format!("{}b", self.file_name));
                                        return Universal::err_exit(x.to_string());
                                    }
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
                warnings
                    .iter()
                    .cloned()
                    .for_each(|x| temp.push_str(&(x + "\n")));
                temp
            });
            if !OS.contains("windows") {
                print!("\n\u{001B}[0m");
            }
        }
        if !errors.is_empty() {
            Universal::err_exit({
                let mut temp: String = String::new();
                errors
                    .iter()
                    .cloned()
                    .for_each(|x| temp.push_str(&(x + "\n")));
                temp
            });
        }
        if let Err(x) = writer.flush() {
            Universal::err_exit(x.to_string());
        }
    }

    fn substitute_strings_and_labels(
        real_line: &Vec<String>,
        labels: &mut HashMap<String, u8>,
        default_memory_map: &MemoryMap,
    ) -> Vec<String> {
        if real_line[0].to_lowercase().eq("label") {
            Self::assign_labels(real_line, labels);
            return Vec::<String>::new();
        }
        Self::substitute_strings(real_line, labels, default_memory_map)
    }
    fn substitute_strings(
        real_line: &[String],
        labels: &HashMap<String, u8>,
        default_memory_map: &MemoryMap,
    ) -> Vec<String> {
        let mut out: Vec<String> = Vec::<String>::new();
        real_line.iter().for_each(|x| {
            if x.starts_with('\"') && x.ends_with('\"') {
                let temp: String = x[1..x.len() - 1].to_string();
                Universal::convert_to_mem(&temp, true, labels, default_memory_map)
                    .iter()
                    .cloned()
                    .for_each(|x2| {
                        out.push(x2);
                    });
            } else if x.starts_with("${") && x.ends_with('}') {
                let key: String = x[2..x.len() - 1].to_string();
                if let Some(x) = labels.get(&key) {
                    out.push(x.to_string());
                } else {
                    Universal::err_exit(Universal::format_error(
                        real_line,
                        &[
                            "Memory Index Label Does Not Exist Or Has Been Replaced",
                            &real_line[1],
                            "Should Be Replaced With The Appropriate Label Or A Memory Index",
                        ],
                    ));
                }
            } else {
                out.push(x.to_string());
            }
        });
        out
    }
    fn assign_labels(real_line: &Vec<String>, labels: &mut HashMap<String, u8>) {
        if real_line.len() != 3 {
            Universal::err_exit(Universal::format_error(
                real_line,
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
                    real_line,
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
                real_line,
                &[
                    "Memory Index",
                    &real_line[1],
                    "Is Larger Than 255 And Will Not Point To Memory",
                ],
            ));
        }
        let label: String = real_line[2].replace("[${}]", "");
        let label_val: u8 = label_mem_ind.try_into().unwrap();
        labels.retain(|_, &mut x| x != label_val);
        labels.insert(label, label_val);
    }

    fn split_line(line: &str) -> Vec<String> {
        let mut out: Vec<String> = Vec::<String>::new();
        let mut buf: String = String::new();
        line.to_string().chars().for_each(|x| {
            if "[-|, \t]".contains(x) {
                if !buf.is_empty() {
                    out.push(buf.clone());
                    buf = String::new();
                }
            } else {
                buf.push(x);
            }
        });
        if !buf.is_empty() {
            out.push(buf);
        }
        out
    }

    fn extract_useful_from_line(
        inside_multiline_comment: bool,
        code: &str,
    ) -> LineExtractionResult {
        let mut multiline_comment = inside_multiline_comment;
        let mut out: String = code.to_string();
        if !inside_multiline_comment {
            while let Some(x) = out.find("/*") {
                let s1: String = out[..x].to_string();
                let s2: String = out[x + 2..].to_string();
                if let Some(y) = s2.find("*/") {
                    out.clear();
                    out.push_str(&s1);
                    out.push_str(&s2[y + 2..]);
                } else {
                    out = s1.to_string();
                    multiline_comment = true;
                }
            }
        } else if let Some(x) = out.find("*/") {
            out = out[x + 2..].to_string();
            multiline_comment = false;
        } else {
            out.clear();
        }
        LineExtractionResult {
            res: Self::remove_line_comment(
                &Self::convert_dividers_in_string(&out).replace("[-|,\t]", " "),
            )
            .trim()
            .to_string(),
            multiline_comment,
        }
    }

    fn remove_line_comment(code: &str) -> String {
        if let Some(y) = code.find("//") {
            return code[..y].to_string();
        }
        code.to_string()
    }

    fn convert_dividers_in_string(line: &str) -> String {
        // https://stackoverflow.com/a/70877609/16915219
        let chars: Chars = line.chars();
        let char_count: usize = chars.clone().count();
        if let Some(x) = chars.rev().position(|c| c == '\"') {
            let first_index: usize = line.find('\"').unwrap();
            let last_index: usize = char_count - x - 1;
            return format!(
                "{}{}{}",
                &line[..first_index],
                Self::escape_dividers_in_string(line[first_index..last_index].to_string()),
                &line[last_index..]
            );
        }
        line.to_string()
    }
    fn escape_dividers_in_string(input: String) -> String {
        let mut res: String = String::new();
        input.chars().for_each(|x| {
            let c: &str = &x.to_string();
            res.push_str(match c {
                "-" => "UU0045",
                "|" => "UU0124",
                "," => "UU0044",
                " " => "UU0032",
                "\t" => "UU0009",
                _ => c,
            });
        });
        res
    }
}