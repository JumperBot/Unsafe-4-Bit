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

use std::fs;
use std::fs::File;
use std::io::Write;

use crate::command::Command;
use crate::memory_map::MemoryMap;
use crate::universal::Universal;

use regex::Regex;

pub struct UFBC{
    pub file_name: String
}

impl UFBC{
    pub fn compile(&self){
        let code: String=match fs::read_to_string(&self.file_name){
            Ok(x)  => Self::remove_useless(&x),
            Err(x) => {
                Universal::err_exit(
                    format!(
                        "{}{}\n{}",
                        "File Provided Does Not Exist...\n",
                        x.to_string(),
                        "Terminating..."
                    )
                );
                return ();
            }
        };
        let dividers: Regex=Regex::new("[-|, \t]").unwrap();
        let lines: Vec<String>=Self::get_lines(&code, &Regex::new("(.*)(\".*\")(.*)").unwrap(), &dividers);
        let mut warnings: Vec<String>=Vec::<String>::new();
        let mut errors: Vec<String>=Vec::<String>::new();
        let mut compiled: Vec<u8>=Vec::<u8>::new();
        let mut memory_map: MemoryMap=MemoryMap::new();
        let default_memory_map: MemoryMap=MemoryMap{
            keys: vec!(
                " ",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "\n"
            ).into_iter().map(|x| x.to_string()).collect::<Vec<String>>(),
            mems: (0..38).collect::<Vec<u64>>(),
        };
        let binary_map: MemoryMap=MemoryMap{
            keys: vec!(
                "wvar", "nvar", "trim",
                "add", "sub", "mul", "div", "mod", "rmod",
                "nop",
                "jm", "jl", "je", "jne",
                "print", "read",
                "wfile", "rfile", "dfile",
                "wfunc", "dfunc"
            ).into_iter().map(|x| x.to_string()).collect::<Vec<String>>(),
            mems: (0..21).collect::<Vec<u64>>(),
        };
        let label_invalids: Regex=Regex::new("[${}]").unwrap();
        for x in lines.clone(){
            let real_line: Vec<String>=Self::split_line(&x, &dividers);
            let line: Vec<String>=Self::substitute_strings_and_labels(
                &real_line, &mut memory_map, &label_invalids, &default_memory_map
            );
            let command: String=real_line[0].clone().to_lowercase();
            if !command.eq("label"){
                if line.len()<2&&!(
                    command.eq("nop")||line[0].eq("\n")||line[0].trim().is_empty()
                ){
                    warnings.push(
                        format!(
                            "Warning{}",
                            {
                                let temp: String=Universal::format_error(
                                    &line, &[
                                        "Command", &real_line[0],
                                        "Will Be Ignored For It Has No Arguments"
                                    ]
                                );
                                temp[5..].to_string()
                            }
                        )
                    );
                }else{
                    let command: Command=Command::new(&line, &real_line, &binary_map);
                    if !command.errors.is_empty(){
                        errors.push(command.errors);
                    }else{
                        for x in command.compiled{
                            compiled.push(x);
                        }
                    }
                }
            }
        }
        if !warnings.is_empty(){
            print!("\u{001B}[93m");
            for x in warnings{
                println!("{x}");
            }
            print!("\n\u{001B}[0m");
        }
        if !errors.is_empty(){
            Universal::err_exit({
                let mut temp: String=String::new();
                for x in errors{
                    temp=format!("{temp}{x}\n");
                }
                temp
            });
        }
        match File::create(format!("{}b", &self.file_name)){
            Ok(mut x)  =>   if let Err(y)=x.write_all(&compiled){
                                Universal::err_exit(y.to_string());
                            },
            Err(x)     =>   Universal::err_exit(x.to_string())
        };
    }

    fn substitute_strings_and_labels(
        real_line: &Vec<String>, labels: &mut MemoryMap,
        label_invalids: &Regex, default_memory_map: &MemoryMap
    ) -> Vec<String>{
        if real_line[0].to_lowercase().eq("label"){
            Self::assign_labels(&real_line, labels, &label_invalids);
            return Vec::<String>::new();
        }
        return Self::substitute_strings(&real_line, &labels, &default_memory_map);
    }
    fn substitute_strings(real_line: &Vec<String>, labels: &MemoryMap, default_memory_map: &MemoryMap) -> Vec<String>{
        let mut out: Vec<String>=Vec::<String>::new();
        for x in real_line.clone(){
            if x.starts_with("\"")&&x.ends_with("\""){
                let temp: String=x.clone()[1..].to_string();
                for x2 in Universal::convert_to_mem(&temp[..temp.len()-1], true, &labels, &default_memory_map){
                    out.push(x2);
                }
            }else if x.starts_with("${")&&x.ends_with("}"){
                let key: String={
                    let temp: String=x.clone()[2..].to_string();
                    temp[..temp.len()-1].to_string()
                };
                if labels.contains_key(&key){
                    out.push(labels.get(&key).to_string());
                }else{
                    Universal::err_exit(Universal::format_error(
                        &real_line, &[
                            "Memory Index Label Already Replaced By Another",
                            &real_line[1], "Should Be Replaced With The Appropriate Label"
                        ]
                    ));
                }
            }else{
                out.push(x);
            }
        }
        return out;
    }
    fn assign_labels(real_line: &Vec<String>, labels: &mut MemoryMap, label_invalids: &Regex){
        if real_line.len()!=3{
            Universal::err_exit(Universal::format_error(
                &real_line, &[
                    "Command", "label",
                    "Needs No Less And No More Than Two Arguments To Work"
                    ]
                ));
        }
        let label_mem_ind: u64=match real_line[1].parse::<u64>(){
            Ok(x)  => x,
            Err(_x) => {
                Universal::err_exit(Universal::format_error(
                    &real_line, &[
                        "Memory Index Expected Instead Of", &real_line[1],
                        "Should Be Replaced With A Memory Index"
                    ]
                ));
                0
            }
        };
        if label_mem_ind>255{
            Universal::err_exit(Universal::format_error(
                &real_line, &[
                    "Memory Index", &real_line[1],
                    "Is Larger Than 255 And Will Not Point To Memory"
                ]
            ));
        }
        let label: String=label_invalids.replace_all(&real_line[2], "").to_string();
        labels.remove_mem_if_exists(&label_mem_ind);
        labels.put(&label, &label_mem_ind);
    }

    fn split_line(line: &str, dividers: &Regex) -> Vec<String>{
        let mut out: Vec<String>=dividers.split(line).into_iter().map(|s| s.to_string()).collect::<Vec<String>>();
        out.retain(|s| s.clone().len()>0);
        return out;
    }

    fn get_lines(code: &str, string: &Regex, dividers: &Regex) -> Vec<String>{
        let line: Regex=Regex::new("(\n)+").unwrap();
        let orig_lines: Vec<String>=line.split(code)
                                      .into_iter()
                                      .map(|s| s.to_string())
                                      .collect::<Vec<String>>();
        return Self::replace_dividers(
            &Self::convert_dividers_in_string(&orig_lines, &string, &dividers), &dividers
        );
    }

    fn replace_dividers(lines: &Vec<String>, dividers: &Regex) -> Vec<String>{
        let mut out: Vec<String>=Vec::<String>::new();
        for line in lines{
            out.push(dividers.replace_all(&line, " ").to_string());
        }
        return out;
    }

    fn remove_useless(code: &str) -> String{
        let comments: Regex=Regex::new("//[^\n]+").unwrap();
        let multi_liners: Regex=Regex::new("/\\*(?:.|\n)*?+\\*/").unwrap();
        let empty_lines: Regex=Regex::new("\n{2,}").unwrap();
        let empty_end_line: Regex=Regex::new("\n$").unwrap();
        let empty: String=String::new();
        return empty_end_line.replace(
            &empty_lines.replace_all(
                &comments.replace_all(
                    &multi_liners.replace_all(
                        code, &empty
                    ).to_string(), &empty
                ).to_string(), "\n"
            ).to_string(), ""
        ).to_string();
    }

    fn convert_dividers_in_string(
        lines: &Vec<String>, string: &Regex, dividers: &Regex
    ) -> Vec<String>{
        let mut out: Vec<String>=Vec::<String>::new();
        for line in lines{
            match string.captures(&line){
                None => out.push(line.to_string()),
                Some(cap) => out.push(
                    format!(
                        "{}{}{}",
                        cap.get(1).unwrap().as_str(),
                        Self::escape_dividers_in_string(cap.get(2).unwrap().as_str().to_string(), &dividers),
                        cap.get(3).unwrap().as_str()
                    )
                ),
            }
        }
        return out;
    }
    fn escape_dividers_in_string(input: String, dividers: &Regex) -> String{
        let mut res: String=String::new();
        for x in input.chars(){
            res=if dividers.is_match(&String::from(x)){
                format!("{}{}", res, Self::escape_divider(x))
            }else{
                format!("{}{}", res, x)
            };
        }
        return res;
    }
    fn escape_divider(divider: char) -> String{
        return (match divider {
            '-'  => "UU0045",
            '|'  => "UU0124",
            ','  => "UU0044",
            ' '  => "UU0032",
            '\t' => "UU0009",
            _    => "UU0000",
        }).to_string();
    }
}
