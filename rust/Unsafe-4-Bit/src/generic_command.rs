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

pub trait GenericCommand{
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> where Self: Sized;
    fn analyze() -> Result<String, String> where Self: Sized;
    fn compile() -> Vec<u16> where Self: Sized;
}

pub struct EmptyCommand{}

impl GenericCommand for EmptyCommand{
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self>{
        return Box::new(EmptyCommand{});
    }
    fn analyze() -> Result<String, String>{
        return Ok("".to_string());
    }
    fn compile() -> Vec<u16>{
        return Vec::<u16>::new();
    }
}
