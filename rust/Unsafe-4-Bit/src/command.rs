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
use crate::nvar_command::NvarCommand;
use crate::universal::Universal;
use crate::wvar_command::WvarCommand;

pub struct Command{
    pub compiled: Vec<u8>,
    pub errors: String,
    pub cancel_optimization: bool,
}

impl Command{
    pub fn new(line: &Vec<String>, real_line: &Vec<String>, binary_map: &MemoryMap) -> Command{
        let command: Box<dyn GenericCommand>=match binary_map.get(&line[0].to_lowercase()){
            0 => WvarCommand::create(&real_line, &line),
            1 => NvarCommand::create(&real_line, &line),
            _ => EmptyCommand::create(&real_line, &line),
            /*
               case 0:
               return new WvarCommand(line, realLine);
               case 2:
               return new TrimCommand(line, realLine);
               case 3: case 4: case 5: case 6: case 7: case 8:
               return new MathCommand(comInd, line, realLine);
               case 9:
               return new NopCommand(line, realLine);
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
        return Command{
            compiled: command.compile(),
            errors: command.analyze(),
            cancel_optimization: false,
        };
    }
    pub fn check_length(real_line: &Vec<String>, line: &Vec<String>, len_plus_one: usize, errors: String) -> String{
        if line.len()>len_plus_one{
            return format!(
                "{}\n{}",
                errors,
                Universal::format_error(
                    real_line, &[
                        "Command", &line[0],
                        "Has Too Many Arguments",
                    ]
                )
            );
        }
        return errors;
    }
    pub fn check_if_mem_ind(real_line: &Vec<String>, line: &Vec<String>, ind: String, errors: String) -> String{
        let mut out: String=errors.clone();
        match ind.parse::<u64>(){
            Ok(x)  => {
                if x>255{
                    out=format!(
                        "{}\n{}",
                        out,
                        Universal::format_error(
                            real_line, &[
                                "Memory Index", &ind,
                                "Is Larger Than 255 And Will Not Point To Memory"
                            ]
                        )
                    );
                }
            },
            Err(_) => {
                out=format!(
                    "{}\n{}",
                    out,
                    Universal::format_error(
                        &real_line, &[
                            "Memory Index Expected Instead Of", &ind,
                            "Should Be Replaced With A Memory Index"
                        ]
                    )
                );
            }
        };
        return out;
    }
    pub fn check_all_if_mem_ind(real_line: &Vec<String>, line: &Vec<String>, errors: String) -> String{
        let mut out: String=errors.clone();
        for x in 1..line.len(){
            out=Self::check_if_mem_ind(real_line, line, line[x].clone(), out);
        }
        return out;
    }

    pub fn check_if_dangerous_mem_ind(real_line: &Vec<String>, line: &Vec<String>, errors: String) -> String{
        let mut out: String=errors.clone();
        let ind: String=line[1].clone();
        match ind.parse::<u64>(){
            Ok(x) => {
                if x<38{
                    out=format!(
                        "{}\n{}",
                        out,
                        Universal::format_error(
                            line, &[
                                "Memory Index", &ind,
                                "Endangers A Read-Only Memory Index"
                            ]
                        )
                    );
                }
            },
            Err(_) => {
                out=format!(
                    "{}\n{}",
                    out,
                    Universal::format_error(
                        line, &[
                            "Memory Index Expected Instead Of", &ind,
                            "Should Be Replaced With A Memory Index"
                        ]
                    )
                );
            }
        };
        return out;
    }
}
