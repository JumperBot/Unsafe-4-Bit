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
use std::io::Read;
use std::io::Seek;
use std::io::SeekFrom::Start;
use std::str::Chars;
use std::thread;
use std::time::Duration;

pub struct Runner{
    file: File,
    file_size: u64,
    ptr: u64,
    mem_ind: [u8; 256],
    mem: [char; 256]
}

impl Runner{
    pub fn new(file_name: String) -> Runner{
        match File::open(&file_name){
            Err(x) => Universal::err_exit(
                format!(
                    "{}{}\n{}",
                    "File Provided Does Not Exist...\n",
                    x.to_string(),
                    "Terminating..."
                )
            ),
            Ok(x)  => {
                match x.metadata(){
                    Err(y) => Universal::err_exit(
                        y.to_string()
                    ),
                    Ok(y)  => {
                        return Runner{
                            file: x,
                            file_size: y.len(),
                            ptr: 0,
                            mem_ind: [0; 256],
                            mem: Self::init_mem()
                        };
                    }
                };
            }
        };
        return Self::new(file_name);
    }

    pub fn run(&mut self){
        while self.ptr!=self.file_size{
            match self.next(){
                0  => self.wvar(),
                1  => {
                    let ind: u8=self.next();
                    self.nvar(&ind);
                },
                2  => self.trim(),
                9  => thread::sleep(Duration::from_millis(10)),
                14 => self.print(),
                _  => break
            }
        }
        println!(
            "{}\n\n{}",
            Universal::arr_to_string(&self.mem),
            Universal::arr_to_string(&self.mem_ind)
        );
    }
    
    fn wvar(&mut self){
        let arg_count: u8=self.next()-1;
        let ind: u8=self.next();
        let resident: Vec<char>=self.rvar(&ind);
        let mut out: String=String::new();
        for _ in 0..arg_count{
            let ptr: u8=self.next();
            if ptr==ind{
                for x in &resident{
                    out=format!("{}{}", out, x);
                }
            }else{
                for x in self.rvar(&ptr){
                    out=format!("{}{}", out, x);
                }
            }
        }
        out=Universal::convert_unicode(&out);
        let mut chars: Chars=out.chars();
        self.write(&ind, &mut chars);
    }
    fn write(&mut self, ind: &u8, chars: &mut Chars){
        self.nvar(&ind);
        let ind_usize: usize=ind.clone() as usize;
        let len: usize=chars.as_str().len();
        for x in 0..len{
            if x==256{
                self.mem_ind[ind_usize]=255;
                return;
            }
            self.mem[x+ind_usize]=chars.next().unwrap();
        }
        self.mem_ind[ind_usize]=ind+(len as u8)-1;
    }

    fn rvar(&mut self, ind: &u8) -> Vec<char>{
        let ind_usize: usize=ind.clone() as usize;
        if self.mem_ind[ind_usize]==0||self.mem_ind[ind_usize]==ind.clone(){
            return vec!(self.mem[ind_usize].clone());
        }
        let mut out: Vec<char>=Vec::<char>::new();
        for x in ind_usize..=self.mem_ind[ind_usize] as usize{
            out.push(self.mem[x as usize].clone());
        }
        return out;
    }

    fn nvar(&mut self, ind: &u8){
        let ind_usize: usize=ind.clone() as usize;
		if self.mem_ind[ind_usize]==0{
            return;
        }
        for x in ind_usize..self.mem_ind[ind_usize] as usize{
            self.mem[x]='\u{0000}';
        }
		self.mem_ind[ind_usize]=0;
    }

    fn trim(&mut self){
        let ind: u8=self.next();
        let trim_size: u8=self.next();
        if trim_size==0{
            self.nvar(&ind);
            return;
        }
        let resident: Vec<char>=self.rvar(&ind);
        if trim_size as usize>=resident.len(){
            return;
        }
        let mut out: String=String::new();
        for x in 0..trim_size as usize{
            out=format!("{}{}", out, resident[x]);
        }
        self.write(&ind, &mut out.chars());
    }
    
    fn print(&mut self){
        let arg_count: u8=self.next();
        let mut out: String=String::new();
		for _ in 0..arg_count as usize{
            let ind: u8=self.next();
            for x in self.rvar(&ind){
                out=format!(
                    "{}{}",
                    out,
                    x
                );
            }
        }
        print!("{}", Universal::convert_unicode(&out));
    }

    fn next(&mut self) -> u8{
        let mut buf: [u8; 1]=[0; 1];
        match self.file.seek(Start(self.ptr)){
            Ok(_)  => (),
            Err(x) => Universal::err_exit(x.to_string())
        };
        match self.file.read_exact(&mut buf){
            Ok(_)  => (),
            Err(x) => Universal::err_exit(x.to_string())
        };
        self.ptr+=1;
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
