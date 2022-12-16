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

use crate::generic_command::EmptyCommand;
use crate::generic_command::GenericCommand;
use crate::memory_map::MemoryMap;
use crate::math_command::MathCommand;
use crate::nop_command::NopCommand;
use crate::nvar_command::NvarCommand;
use crate::trim_command::TrimCommand;
use crate::universal::Universal;
use crate::wvar_command::WvarCommand;

use itertools::Itertools;

pub struct Command{
    pub compiled: Vec<u8>,
    pub errors: String,
    pub cancel_optimization: bool,
}

impl Command{
    pub fn new(line: &Vec<String>, real_line: &Vec<String>, binary_map: &MemoryMap) -> Command{
        let command: Box<dyn GenericCommand>=match binary_map.get(&line[0].to_lowercase()){
            0       => WvarCommand::create(&real_line, &line),
            1       => NvarCommand::create(&real_line, &line),
            2       => TrimCommand::create(&real_line, &line),
            (3..=8) => MathCommand::create(&real_line, &line),
            9       => NopCommand::create(&real_line, &line),
            _       => EmptyCommand::create(&real_line, &line),
            /*
               case 10: case 11: case 12: case 13:
               return new JumpCommand(comInd, line, realLine);
               case 14:
               return new PrintCommand(line, realLine);
               case 15:
               cancelOptimization=true;
               case 1:
               return new NeedsOneMemCommand(comInd, line, realLine);
               case 17:
               cancelOptimization=true;
               return new RfileCommand(line, realLine);
               case 18:
               case 19:
               case 20:
               case 16:
               cancelOptimization=true;
               return new NeedsArgLengthCommand(comInd, line, realLine);
            */
        };
        let err: String=command.analyze();
        if err.len()!=0{
            return Command{
                compiled: command.compile(),
                errors: err,
                cancel_optimization: false
            };
        }
        return Command{
            compiled: command.compile(),
            errors: String::new(),
            cancel_optimization: false,
        };
    }
    pub fn check_arg_length_using_limit(real_line: &Vec<String>, line: &Vec<String>, limit: usize) -> String{
        if line.len()-1>limit{
            return Universal::format_error(
                real_line, &[
                    "Command", &line[0],
                    &format!(
                        "{}{}{}",
                        "Has More Than The Maximum ",
                        limit,
                        " Allowed Arguments"
                    )
                ]
            );
        }
        return String::new();
    }
    pub fn check_arg_length(real_line: &Vec<String>, line: &Vec<String>, len: usize) -> String{
        if line.len()!=len+1{
            println!("{}", line.len()-1);
            println!("{}", Universal::arr_to_string(&line));
            return Universal::format_error(
                real_line, &[
                    "Command", &line[0],
                    &format!(
                        "{}{}{}",
                        "Needs No Less And No More Than ",
                        match len{
                            0 => "Zero",
                            1 => "One",
                            2 => "Two",
                            _ => "Three",
                        },
                        " Arguments To Work"
                    )
                ]
            );
        }
        return String::new();
    }
    pub fn check_if_mem_ind(real_line: &Vec<String>, ind: String) -> String{
        match ind.parse::<u64>(){
            Ok(x)  => {
                if x>255{
                    return Universal::format_error(
                        real_line, &[
                            "Memory Index", &ind,
                            "Is Larger Than 255 And Will Not Point To Memory"
                        ]
                    );
                }
            },
            Err(_) => {
                return Universal::format_error(
                    &real_line, &[
                        "Memory Index Expected Instead Of", &ind,
                        "Should Be Replaced With A Memory Index"
                    ]
                );
            }
        };
        return String::new();
    }
    pub fn check_all_if_mem_ind(real_line: &Vec<String>, line: &Vec<String>) -> String{
        let mut out: String=String::new();
        for x in 1..line.len(){
            out=Self::check_if_mem_ind(real_line, line[x].clone());
        }
        return out;
    }

    pub fn check_if_dangerous_mem_ind(real_line: &Vec<String>, ind: String) -> String{
        match ind.parse::<u64>(){
            Ok(x) => {
                if x<38{
                    return Universal::format_error(
                        &real_line, &[
                            "Memory Index", &ind,
                            "Endangers A Read-Only Memory Index"
                        ]
                    );
                }
            },
            Err(_) => {
                return Universal::format_error(
                    &real_line, &[
                        "Memory Index Expected Instead Of", &ind,
                        "Should Be Replaced With A Memory Index"
                    ]
                );
            }
        };
        return String::new();
    }

    pub fn errors_to_string(vec: Vec<String>) -> String{
        let mut out: String=String::new();
        for x in vec.iter().unique(){
            out=format!(
                "{}\n{}",
                out,
                x
            );
        }
        return out.trim().to_string();
    }
}
