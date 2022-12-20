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
use crate::memory_map::MemoryMap;

pub struct Universal {}

impl Universal {
    pub fn convert_u32_to_char(code: u32) -> char {
        return match char::from_u32(code) {
            None => '\u{0000}',
            Some(c) => c,
        };
    }

    pub fn arr_to_string<T: std::fmt::Debug>(arr: &[T]) -> String {
        let mut out: String = String::new();
        for x in arr {
            out = format!("{out}{:?}, ", x);
        }
        return out[..out.len() - 2].to_string();
    }

    pub fn err_exit(err_msg: String) {
        println!("\u{001B}[91m{err_msg}\u{001B}[0m");
        std::process::exit(1);
    }

    pub fn manage_padding(input: String, padding: usize) -> String {
        return format!("{:0>width$}", input, width = padding);
    }

    pub fn format_error(line: &Vec<String>, input: &[&str]) -> String {
        let mut arr: String = Self::arr_to_string(line)[1..].to_string();
        arr = arr[..arr.len() - 1].to_string().replace("\", \"", " ");
        return format!(
            "Error: |\n    {}: |\n        \"{}\" {}: |\n            {arr}",
            input[0], input[1], input[2]
        );
    }

    pub fn convert_to_mem(
        input: &str,
        contains_labels: bool,
        labels: &MemoryMap,
        mem_map: &MemoryMap,
    ) -> Vec<String> {
        let mut out: Vec<String> = Vec::<String>::new();
        let mut back_slash: bool = false;
        let u: String = "21".to_string();
        if !contains_labels {
            for x in input.chars() {
                if mem_map.contains_key(&x.to_string()) {
                    out.push(mem_map.get(&x.to_string()).to_string());
                } else {
                    if x == '\\' {
                        if back_slash {
                            back_slash = false;
                            out.push(u.clone());
                            out.push(u.clone());
                            for x2 in Self::manage_padding(('\\' as u32).to_string(), 4).chars() {
                                out.push(mem_map.get(&x2.to_string()).to_string());
                            }
                        } else {
                            back_slash = true;
                        }
                    } else if back_slash {
                        if x == 'n' {
                            out.push("37".to_string());
                        } else {
                            out.push(u.clone());
                            out.push(u.clone());
                            for x2 in Self::manage_padding((x as u32).to_string(), 4).chars() {
                                out.push(mem_map.get(&x2.to_string()).to_string());
                            }
                        }
                        back_slash = false;
                    } else {
                        out.push(u.clone());
                        out.push(u.clone());
                        for x2 in Self::manage_padding((x as u32).to_string(), 4).chars() {
                            out.push(mem_map.get(&x2.to_string()).to_string());
                        }
                    }
                }
            }
            return out;
        };
        let mut mem_indicator: bool = false;
        let mut is_label: bool = false;
        let mut place_holder: String = String::new();
        for x in input.chars() {
            if x == '$' {
                mem_indicator = true;
                place_holder = format!("{}{}", place_holder, x);
            } else if mem_indicator {
                place_holder = format!("{}{}", place_holder, x);
                if x == '{' {
                    is_label = true;
                } else if is_label {
                    if x == '}' {
                        let key: String = {
                            let temp: String = place_holder.clone()[2..].to_string();
                            temp[..temp.len() - 1].to_string()
                        };
                        if labels.contains_key(&key) {
                            out.push(labels.get(&key).to_string());
                        } else {
                            Self::err_exit(Self::format_error(
                                &vec![Self::convert_unicode(&input)],
                                &[
                                    "Memory Index Label Already Replaced By Another",
                                    &place_holder,
                                    "Should Be Replaced With The Appropriate Label",
                                ],
                            ));
                        }
                        place_holder = String::new();
                        mem_indicator = false;
                        is_label = false;
                    }
                } else if !Self::is_digit(x) {
                    mem_indicator = false;
                    for converted in Self::convert_to_mem(&place_holder, false, &labels, &mem_map) {
                        out.push(converted);
                    }
                    place_holder = String::new();
                } else {
                    if place_holder.len() == 4 {
                        out.push(place_holder[1..].to_string());
                        place_holder = String::new();
                        mem_indicator = false;
                    }
                }
            } else if mem_map.contains_key(&x.to_string()) {
                out.push(mem_map.get(&x.to_string()).to_string());
            } else {
                if x == '\\' {
                    if back_slash {
                        back_slash = false;
                        out.push(u.clone());
                        out.push(u.clone());
                        for x2 in Self::manage_padding((x as u32).to_string(), 4).chars() {
                            out.push(mem_map.get(&x2.to_string()).to_string());
                        }
                    } else {
                        back_slash = true;
                    }
                } else if back_slash {
                    if x == 'n' {
                        out.push("37".to_string());
                    } else {
                        out.push(u.clone());
                        out.push(u.clone());
                        for x2 in Self::manage_padding((x as u32).to_string(), 4).chars() {
                            out.push(mem_map.get(&x2.to_string()).to_string());
                        }
                    }
                    back_slash = false;
                } else {
                    out.push(u.clone());
                    out.push(u.clone());
                    for x2 in Self::manage_padding((x as u32).to_string(), 4).chars() {
                        out.push(mem_map.get(&x2.to_string()).to_string());
                    }
                }
            }
        }
        return out;
    }
    pub fn is_digit(c: char) -> bool {
        let val: u32 = c as u32;
        return val > 47 && val < 58;
    }
    pub fn convert_unicode(input: &str) -> String {
        if input.len() < 6 {
            return input.to_string();
        }
        let mut out: String = String::new();
        let mut possible_match: bool = false;
        let mut place_holder: String = String::new();
        for x in input.chars() {
            if place_holder.len() == 6 {
                out = format!(
                    "{out}{}",
                    Self::convert_u32_to_char(place_holder[2..].parse::<u32>().unwrap())
                );
                place_holder = String::new();
                possible_match = false;
            }
            if possible_match {
                if x.to_lowercase().to_string().eq("u") {
                    if place_holder.len() != 1 {
                        possible_match = false;
                        out = format!("{out}{place_holder}{x}");
                        place_holder = String::new();
                    } else {
                        place_holder = format!("{place_holder}{x}");
                    }
                } else if Self::is_digit(x.clone()) {
                    if place_holder.len() == 1 {
                        possible_match = false;
                        out = format!("{out}{place_holder}{x}");
                        place_holder = String::new();
                    } else {
                        place_holder = format!("{place_holder}{x}");
                    }
                } else {
                    possible_match = false;
                    out = format!("{out}{place_holder}{x}");
                    place_holder = String::new();
                }
            } else if x.to_lowercase().to_string().eq("u") {
                possible_match = true;
                place_holder = x.to_string();
            } else {
                out = format!("{out}{x}");
            }
            if place_holder.len() == 6 {
                out = format!(
                    "{out}{}",
                    Self::convert_u32_to_char(place_holder[2..].parse::<u32>().unwrap())
                );
                place_holder = String::new();
                possible_match = false;
            }
        }
        if !place_holder.is_empty() {
            out = format!("{out}{place_holder}");
        }
        return out;
    }
}
