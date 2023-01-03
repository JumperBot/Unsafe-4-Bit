use crate::memory_map::MemoryMap;

use std::collections::HashMap;
use std::env::consts::OS;

pub struct Universal {}

impl Universal {
    pub fn convert_u32_to_char(code: u32) -> char {
        char::from_u32(code).unwrap_or('\0')
    }

    pub fn arr_to_string<T: std::fmt::Display>(arr: &[T]) -> String {
        let mut out: String = String::new();
        arr.iter()
            .for_each(|x| out.push_str(&(x.to_string() + ",")));
        out[..out.len() - 2].to_string()
    }

    pub fn err_exit(err_msg: String) {
        if !OS.contains("windows") {
            println!("\u{001B}[91m{err_msg}\u{001B}[0m");
        } else {
            println!("{err_msg}");
        }
        std::process::exit(1);
    }

    pub fn manage_padding(input: String, padding: usize) -> String {
        format!("{:0>width$}", input, width = padding)
    }

    pub fn format_error(line: &[String], input: &[&str]) -> String {
        let arr: String = Self::arr_to_string(line).replace(", ", " ");
        format!(
            "Error: |\n    {}: |\n        \"{}\" {}: |\n            {arr}",
            input[0], input[1], input[2]
        )
    }

    pub fn convert_to_mem(
        input: &str,
        contains_labels: bool,
        labels: &HashMap<String, u8>,
        mem_map: &MemoryMap,
    ) -> Vec<String> {
        let mut out: Vec<String> = Vec::<String>::new();
        let mut back_slash: bool = false;
        let u: String = "21".to_string();
        if !contains_labels {
            for x in input.chars() {
                if mem_map.contains_key(&x.to_string()) {
                    out.push(mem_map.get(&x.to_string()).to_string());
                } else if x == '\\' {
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
                    match x {
                        'n' => out.push("37".to_string()),
                        'r' => {
                            out.push(u.clone());
                            out.push(u.clone());
                            for x2 in "0032".chars() {
                                out.push(mem_map.get(&x2.to_string()).to_string());
                            }
                        }
                        'f' => {
                            out.push(u.clone());
                            out.push(u.clone());
                            for x2 in "0012".chars() {
                                out.push(mem_map.get(&x2.to_string()).to_string());
                            }
                        }
                        'b' => {
                            out.push(u.clone());
                            out.push(u.clone());
                            for x2 in "0008".chars() {
                                out.push(mem_map.get(&x2.to_string()).to_string());
                            }
                        }
                        _ => {
                            out.push(u.clone());
                            out.push(u.clone());
                            for x2 in Self::manage_padding((x as u32).to_string(), 4).chars() {
                                out.push(mem_map.get(&x2.to_string()).to_string());
                            }
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
            return out;
        };
        let mut mem_indicator: bool = false;
        let mut is_label: bool = false;
        let mut place_holder: String = String::new();
        for x in input.chars() {
            if x == '$' {
                mem_indicator = true;
                place_holder.push(x);
            } else if mem_indicator {
                place_holder.push(x);
                if x == '{' {
                    is_label = true;
                } else if is_label {
                    if x == '}' {
                        let key: String =
                            place_holder.clone()[2..place_holder.len() - 1].to_string();
                        if let Some(x) = labels.get(&key) {
                            out.push(x.to_string());
                        } else {
                            Self::err_exit(Self::format_error(
                                &[Self::convert_unicode(input)],
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
                    for converted in Self::convert_to_mem(&place_holder, false, labels, mem_map) {
                        out.push(converted);
                    }
                    place_holder = String::new();
                } else if place_holder.len() == 4 {
                    out.push(place_holder[1..].to_string());
                    place_holder = String::new();
                    mem_indicator = false;
                }
            } else if mem_map.contains_key(&x.to_string()) {
                out.push(mem_map.get(&x.to_string()).to_string());
            } else if x == '\\' {
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
        out
    }
    pub fn is_digit(c: char) -> bool {
        let val: u32 = c as u32;
        val > 47 && val < 58
    }
    pub fn quick_parse(input: String) -> u32 {
        let mut out: u32 = 0;
        input.chars().for_each(|x| {
            out += x as u32 - 48;
            out *= 10;
        });
        out / 10
    }
    pub fn convert_unicode(input: &str) -> String {
        if input.len() < 6 {
            return input.to_string();
        }
        let mut out: String = input.to_string();
        let mut lowercase_out: String = input.to_lowercase();
        while let Some(x) = lowercase_out.find("uu") {
            if x + 5 >= out.len() {
                break;
            }
            let mut place_holder: String = out[x + 2..x + 6].to_string();
            for x in place_holder.chars() {
                if !Self::is_digit(x) {
                    place_holder = String::new();
                    break;
                }
            }
            if !place_holder.is_empty() {
                {
                    let s1: String = out[..x].to_string();
                    let s2: String =
                        Self::convert_u32_to_char(Self::quick_parse(place_holder)).to_string();
                    let s3: String = out[x + 6..].to_string();
                    out.clear();
                    out.push_str(&(s1 + &s2 + &s3));
                }
                let s1: String = lowercase_out[..x].to_string();
                let s2: String = "_".to_string();
                let s3: String = lowercase_out[x + 6..].to_string();
                lowercase_out.clear();
                lowercase_out.push_str(&(s1 + &s2 + &s3));
            } else {
                let s1: String = lowercase_out[..x].to_string();
                let s2: String = "______".to_string();
                let s3: String = lowercase_out[x + 6..].to_string();
                lowercase_out.clear();
                lowercase_out.push_str(&(s1 + &s2 + &s3));
            }
        }
        out
    }
}
