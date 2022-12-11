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

pub struct Command<'a>{
    pub compiled: Vec<u16>,
    pub errors: String,
    pub cancel_optimization: bool,
    binary_map: &'a MemoryMap,
    line: &'a Vec<String>,
    real_line: &'a Vec<String>,
}

impl Command<'_>{
    pub fn new<'a>(line: &'a Vec<String>, real_line: &'a Vec<String>, binary_map: &'a MemoryMap) -> Command<'a>{
        match binary_map.get(&line[0]){
            _ => ()
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
               default:
               return null;
            */
        }
        return Command{
            compiled: Vec::<u16>::new(),
            errors: String::new(),
            cancel_optimization: false,
            binary_map: binary_map,
            line: line,
            real_line: real_line
        };
    }
}
