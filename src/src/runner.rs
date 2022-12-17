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

use crate::universal::Universal;

pub struct Runner{
    file_name: String,
    mem_ind: Box<[u8]>,
    mem: Box<[char]>
}

impl Runner{
    pub fn new(file_name: String) -> Runner{
        return Runner{
            file_name: file_name,
            mem_ind: Box::new([0; 256]),
            mem: Self::init_mem()
        }
    }
    pub fn run(&self){
        println!("Memory:");
        println!("{}", Universal::arr_to_string(&self.mem));
        println!("Memory Index Bounds:");
        println!("{}", Universal::arr_to_string(&self.mem_ind));
    }
    
    fn init_mem() -> Box<[char]>{
        let mut mem: [char;256]=['\u{0000}'; 256];
        mem[0]=' ';
        let mut i: usize=0;
        while i != 26{
            mem[i+1]=Universal::convert_u32_to_char(('A' as u32)+i as u32);
            i+=1;
        }
        i=0;
        while i != 10{
            mem[i+27]=Universal::convert_u32_to_char(('0' as u32)+i as u32);
            i+=1;
        }
        mem[37]='\n';
        return Box::new(mem);
    }
}
