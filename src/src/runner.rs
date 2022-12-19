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
use std::str::Chars;
use std::thread;
use std::time::Duration;
use std::time::SystemTime;
use std::time::UNIX_EPOCH;

pub struct Runner {
    file: File,
    file_size: u64,
    ptr: u64,
    mem_ind: [u8; 256],
    mem: [char; 256],
}

impl Runner {
    pub fn new(file_name: String) -> Runner {
        let res: Result<File, _> = File::open(&file_name);
        if let Err(ref x) = res {
            Universal::err_exit(format!(
                "File Provided Does Not Exist...\n{}\nTerminating...",
                x.to_string(),
            ));
        }
        let file: File = res.unwrap();
        let res2: Result<Metadata, _> = file.metadata();
        if let Err(ref y) = res2 {
            Universal::err_exit(y.to_string());
        }
        return Runner {
            file: file,
            file_size: res2.unwrap().len(),
            ptr: 0,
            mem_ind: [0; 256],
            mem: Self::init_mem(),
        };
    }

    pub fn run(&mut self) {
        let ten_millis: Duration = Duration::from_millis(10);
        let start: u128 = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_millis();
        while self.ptr != self.file_size {
            let com: u8 = self.next();
            match com {
                0 => self.wvar(),
                1 => {
                    let ind: u8 = self.next();
                    self.nvar(&ind);
                }
                2 => self.trim(),
                3..=8 => self.math(&com),
                9 => thread::sleep(ten_millis.clone()),
                //10..=13 => self.jump(&com),
                14 => self.print(),
                _ => break,
            }
        }
        println!(
            "Took {}ms To Interpret The Program",
            SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_millis()
                - start
        );
        println!(
            "{}\n\n{}",
            Universal::arr_to_string(&self.mem),
            Universal::arr_to_string(&self.mem_ind)
        );
    }

    fn wvar(&mut self) {
        let arg_count: u8 = self.next() - 1;
        let ind: u8 = self.next();
        let resident: Vec<char> = self.rvar(&ind);
        let mut out: String = String::new();
        for _ in 0..arg_count {
            let ptr: u8 = self.next();
            if ptr == ind {
                for x in &resident {
                    out = format!("{out}{x}");
                }
            } else {
                for x in self.rvar(&ptr) {
                    out = format!("{out}{x}");
                }
            }
        }
        out = Universal::convert_unicode(&out);
        let mut chars: Chars = out.chars();
        self.write_chars(&ind, &mut chars);
    }
    fn write_chars(&mut self, ind: &u8, chars: &mut Chars) {
        self.nvar(&ind);
        let ind_usize: usize = ind.clone() as usize;
        let len: usize = chars.as_str().len();
        for x in 0..len {
            if x + ind_usize == 256 {
                self.mem_ind[ind_usize] = 255;
                return;
            }
            self.mem[x + ind_usize] = chars.next().unwrap();
        }
        self.mem_ind[ind_usize] = ind + (len as u8) - 1;
    }
    fn write_arr(&mut self, ind: &u8, arr: &[char]) {
        self.nvar(&ind);
        let ind_usize: usize = ind.clone() as usize;
        let len: usize = arr.len();
        for (x, c) in arr.iter().enumerate() {
            let ptr: usize = x + ind_usize;
            if ptr == 256 {
                self.mem_ind[ind_usize] = 255;
                return;
            }
            self.mem[ptr] = c.clone();
        }
        self.mem_ind[ind_usize] = ind + (len as u8) - 1;
    }

    fn rvar(&mut self, ind: &u8) -> Vec<char> {
        let ind_usize: usize = ind.clone() as usize;
        if self.mem_ind[ind_usize] == 0 || self.mem_ind[ind_usize] == ind.clone() {
            return vec![self.mem[ind_usize].clone()];
        }
        let mut out: Vec<char> = Vec::<char>::new();
        for x in &self.mem[ind_usize..=self.mem_ind[ind_usize] as usize] {
            out.push(x.clone());
        }
        return out;
    }

    fn nvar(&mut self, ind: &u8) {
        let ind_usize: usize = ind.clone() as usize;
        if self.mem_ind[ind_usize] == 0 {
            return;
        }
        for x in ind_usize..=self.mem_ind[ind_usize] as usize {
            self.mem[x] = '\u{0000}';
        }
        self.mem_ind[ind_usize] = 0;
    }

    fn trim(&mut self) {
        let ind: u8 = self.next();
        let trim_size: u8 = self.next();
        if trim_size == 0 {
            self.nvar(&ind);
            return;
        }
        let resident: Vec<char> = self.rvar(&ind);
        if trim_size as usize >= resident.len() {
            return;
        }
        self.write_arr(&ind, &resident[0..trim_size as usize]);
    }

    fn math(&mut self, op: &u8) {
        let ind1: u8 = self.next();
        let ind2: u8 = self.next();
        let val1: Vec<char> = self.rvar(&ind1);
        let val2: Vec<char> = self.rvar(&ind2);
        let num1: f64 = Self::to_num(&val1);
        let num2: f64 = Self::to_num(&val2);
        if num2 == 0.0 && op > &5 {
            self.write_arr(&ind1, &['i'; 1]);
            return;
        }
        let out: f64;
        match op {
            3 => out = num1 + num2,
            4 => out = num1 - num2,
            5 => out = num1 * num2,
            6 => out = num1 / num2,
            7 => out = num1 % num2,
            8 => out = ((num1 / num2) as u32) as f64,
            _ => return,
        }
        if out % 1.0 == 0.0 {
            self.write_chars(&ind1, &mut (out as u32).to_string().chars());
        } else {
            self.write_chars(&ind1, &mut out.to_string().chars());
        }
    }
    fn find_period(arr: &[char]) -> Option<usize> {
        for x in 0..arr.len() / 2 + 1 {
            if arr[x] == '.' {
                return Some(x);
            }
            if arr[arr.len() - 1 - x] == '.' {
                return Some(arr.len() - x);
            }
        }
        return None;
    }
    fn to_num(arr: &[char]) -> f64 {
        if let Some(x) = Self::find_period(&arr) {
            let mut out: [f64; 2] = [0.0; 2];
            for y in 0..x {
                let y2 = x + 1 + y;
                if !(Universal::is_digit(arr[y].clone()) || Universal::is_digit(arr[y2].clone())) {
                    return Self::hash(&arr).into();
                }
                out[0] += <u32 as Into<f64>>::into(arr[y] as u32) - 48.0;
                out[0] *= 10.0;
                out[1] += <u32 as Into<f64>>::into(arr[y2] as u32) - 48.0;
                out[1] /= 10.0;
            }
            return (out[0] / 10.0) + out[1];
        }
        let mut out: f64 = 0.0;
        for x in arr {
            if !Universal::is_digit(x.clone()) {
                return Self::hash(&arr).into();
            }
            out += <u32 as Into<f64>>::into(x.clone() as u32) - 48.0;
            out *= 10.0;
        }
        return out / 10.0;
    }
    fn hash(arr: &[char]) -> u32 {
        let mut hash: u32 = 0;
        for x in arr {
            hash = 31 * hash + (x.clone() as u32);
        }
        return hash;
    }

    fn print(&mut self) {
        let arg_count: u8 = self.next();
        let mut out: String = String::new();
        for _ in 0..arg_count as usize {
            let ind: u8 = self.next();
            for x in self.rvar(&ind) {
                out = format!("{out}{x}");
            }
        }
        print!("{}", Universal::convert_unicode(&out));
    }

    fn next(&mut self) -> u8 {
        let mut buf: [u8; 1] = [0; 1];
        if let Err(x) = self.file.seek(Start(self.ptr)) {
            Universal::err_exit(x.to_string());
        }
        if let Err(x) = self.file.read_exact(&mut buf) {
            Universal::err_exit(x.to_string())
        }
        self.ptr += 1;
        return buf[0];
    }

    fn init_mem() -> [char; 256] {
        let mut mem: [char; 256] = ['\u{0000}'; 256];
        mem[0] = ' ';
        for x in 0..26 {
            mem[x + 1] = Universal::convert_u32_to_char(('A' as u32) + x as u32);
        }
        for x in 0..10 {
            mem[x + 27] = Universal::convert_u32_to_char(('0' as u32) + x as u32);
        }
        mem[37] = '\n';
        return mem;
    }
}
