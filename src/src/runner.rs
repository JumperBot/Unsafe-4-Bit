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

use std::fs::File;
use std::fs::Metadata;
use std::io::Read;
use std::io::Seek;
use std::io::SeekFrom::Start;

pub struct Runner{
    file_name: String,
    mem_ind: [u8; 256],
    mem: [char; 256]
}

impl Runner{
    pub fn new(file_name: String) -> Runner{
        return Runner{
            file_name: file_name,
            mem_ind: [0; 256],
            mem: Self::init_mem()
        }
    }
    pub fn run(&mut self){
        let mut file: File=match File::open(&self.file_name){
            Ok(x)  => x,
            Err(x) => {
                Universal::err_exit(
                    format!(
                        "{}{}\n{}",
                        "File Provided Does Not Exist...\n",
                        x.to_string(),
                        "Terminating..."
                    )
                );
                return ();
            }
        };
        match file.metadata(){
            Err(x) => {
                Universal::err_exit(
                    x.to_string()
                );
            },
            Ok(x)  => {
                let metadata: Metadata=x;
                let size: u64=metadata.len();
                let mut buf: [u8; 1]=[0; 1];
                for x in 0..size{
                    println!("{}", Self::next(&mut file, &mut buf, x));
                }
            }
        };
    }
    
    fn next(file: &mut File, buf: &mut [u8; 1], ptr: u64) -> u8{
        match file.seek(Start(ptr)){
            Ok(_)  => (),
            Err(x) => Universal::err_exit(x.to_string())
        };
        match file.read_exact(buf){
            Ok(_)  => (),
            Err(x) => Universal::err_exit(x.to_string())
        };
        return buf[0];
    }
    fn init_mem() -> [char; 256]{
        let mut mem: [char; 256]=['\u{0000}'; 256];
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
        return mem;
    }
}
