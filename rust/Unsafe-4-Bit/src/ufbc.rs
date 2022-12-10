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

use crate::memory_map::MemoryMap;
use crate::universal::Universal;

use regex::Captures;
use regex::Regex;

pub struct UFBC{
    pub file_name: String
}

impl UFBC{
    pub fn compile(&self){
        let code: String=match fs::read_to_string(&self.file_name){
            Ok(content) => Self::remove_useless(&content),
            Err(_e) => {
                Universal::err_exit(
                    concat!(
                        "File Provided Does Not Exist...\n",
                        "Terminating..."
                    ).to_string()
                );
                return ();
            }
        };
        let string: Regex=Regex::new("(.*)(\".*\")(.*)").unwrap();
        let dividers: Regex=Regex::new("[-|, \t]").unwrap();
        let lines: Vec<String>=Self::get_lines(&code, &string, &dividers);
        let mut warnings: Vec<String>=Vec::<String>::new();
        let mut errors: Vec<String>=Vec::<String>::new();
        let mut cancel_optimizations: bool=false;
        let mut compiled: Vec<Vec<u8>>=Vec::<Vec<u8>>::new();
        let mut memory_map: MemoryMap=MemoryMap::new();
        let labelInvalids: Regex=Regex::new("[${}]").unwrap();
        for x in lines.clone(){
            let realTemp: Vec<String>=Self::split_line(&x, &dividers);
            let temp: Vec<String>=Self::substitute_strings_and_labels(&realTemp, &string, &memory_map);
        }
        println!("{}", Universal::arr_to_string2(&compiled, '\n'));
    }

    fn substitute_strings_and_labels(line: &Vec<String>, string: &Regex, labels: &MemoryMap) -> Vec<String>{
        return line.clone();
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
        let empty: String="".to_string();
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
