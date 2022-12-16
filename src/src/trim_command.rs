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

use crate::command::Command;
use crate::generic_command::GenericCommand;
use crate::universal::Universal;

pub struct TrimCommand{
    real_line: Vec<String>,
    line: Vec<String>
}

impl GenericCommand for TrimCommand{
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self>{
        let out: TrimCommand=TrimCommand{
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String{
        let mut errors: Vec<String>=vec!(
            Command::check_if_dangerous_mem_ind(
                &self.real_line, self.line[1].clone()
            ),
            Command::check_if_mem_ind(
                &self.real_line, self.line[1].clone()
            ),
            Command::check_arg_length(
                &self.real_line, &self.line, 2
            ),
        );
        let res: Result<u64, _>=self.line[2].parse::<u64>();
        if res.is_err(){
            errors.push(
                Universal::format_error(
                    &self.line, &[
                        "Trim Length Expected Instead Of", &self.line[2],
                        "Should Be Replaced With A Trim Length"
                    ]
                )
            );
        }
        if res.unwrap()>255{
            errors.push(
                Universal::format_error(
                    &self.line, &[
                        "Trim Length", &self.line[2],
                        "Is Larger Than 255 And Will Not Be Compiled Properly"
                    ]
                )
            );
        }
        return Command::errors_to_string(errors);
    }
    fn compile(&self) -> Vec<u8>{
        return vec!(2, self.line[1].parse::<u8>().unwrap(), self.line[2].parse::<u8>().unwrap());
    }
}
