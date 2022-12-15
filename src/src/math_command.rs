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

pub struct MathCommand{
    real_line: Vec<String>,
    line: Vec<String>,
    ind: u64
}

impl GenericCommand for MathCommand{
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self>{
        let out: MathCommand=MathCommand{
            real_line: real_line.clone(),
            line: line.clone(),
            ind: match line[0].as_str(){
                "add"  => 3,
                "sub"  => 4,
                "mul"  => 5,
                "div"  => 6,
                "mod"  => 7,
                "rmod" => 8,
                _      => 9
            }
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String{
        return Command::check_if_dangerous_mem_ind(
            &self.real_line, self.line[1].clone(), Command::check_all_if_mem_ind(
                &self.real_line, &self.line, Command::check_arg_length(
                    &self.real_line, &self.line, 2, String::new()
                )
            )
        );
    }
    fn compile(&self) -> Vec<u8>{
        return vec!(self.ind as u8, self.line[1].parse::<u8>().unwrap(), self.line[2].parse::<u8>().unwrap());
    }
}
