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

mod command;
mod flag_manager;
mod generic_command;
mod memory_map;
mod nvar_command;
mod trim_command;
mod ufbc;
mod universal;
mod wvar_command;

use flag_manager::FlagManager;
use ufbc::UFBC;
use universal::Universal;

use std::env;

fn main(){
    let flags: FlagManager=FlagManager::new(
        &env::args().collect::<Vec<String>>()
    );
    if flags.file_name.len()==0 {
        Universal::err_exit(
            "No File Input Found, Terminating.".to_string()
        );
    }
    if flags.compile_flag {
        let compiler: UFBC=UFBC{
            file_name: flags.file_name,
        };
        compiler.compile();
    }
}

fn run(){
    let mut mem_ind: [u8; 256]=[0; 256];
    let mut mem: [char; 256]=['\u{0000}'; 256];
    init_mem(&mut mem);
    println!("Memory:");
    println!("{}", Universal::arr_to_string(&mem));
    println!("Memory Index Bounds:");
    println!("{}", Universal::arr_to_string(&mem_ind));
}

fn init_mem(mem: &mut [char]){
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
}
