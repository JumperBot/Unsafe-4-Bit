use crate::universal::Universal;

use std::fs::{self, File};
use std::io::{self, ErrorKind, Read, Seek, SeekFrom::Start, Stdin, Write};
use std::path::Path;
use std::str::Chars;
use std::thread;
use std::time::{Duration, SystemTime, UNIX_EPOCH};

pub struct Runner {
    file: File,
    file_name: String,
    file_size: u64,
    ptr: u64,
    mem_ind: [u8; 256],
    mem: [char; 256],
    byte_ind: Vec<u64>,
    ten_millis: Duration,
    perfmes: bool,
    nanosec: bool,
    commmes: bool,
}

impl Runner {
    pub fn new(file_name: String, perfmes: bool, nanosec: bool, commmes: bool) -> Runner {
        match File::open(&file_name) {
            Err(x) => Universal::err_exit(format!(
                "File Provided Does Not Exist...\n{x}\nTerminating..."
            )),
            Ok(x) => match x.metadata() {
                Err(y) => Universal::err_exit(y.to_string()),
                Ok(y) => {
                    return Runner {
                        file: x,
                        file_name,
                        file_size: y.len(),
                        ptr: 0,
                        mem_ind: [0; 256],
                        mem: Self::init_mem(),
                        byte_ind: Vec::<u64>::new(),
                        ten_millis: Duration::from_millis(10),
                        perfmes,
                        nanosec,
                        commmes,
                    }
                }
            },
        }
        Self::new(file_name, perfmes, nanosec, commmes)
    }

    pub fn run(&mut self) {
        if self.perfmes {
            let start: u128;
            match SystemTime::now().duration_since(UNIX_EPOCH) {
                Ok(x) => {
                    if self.nanosec {
                        start = x.as_nanos();
                    } else {
                        start = x.as_millis();
                    }
                }
                Err(_) => start = 0,
            }
            if self.commmes {
                self.run_commands_with_time();
            } else {
                self.run_commands();
            }
            let mut mem_leaks: String = String::new();
            for i in 0..32 {
                for ratio in 0..8 {
                    let ind: usize = i + (ratio * 32);
                    if self.mem_ind[ind] != 0 {
                        mem_leaks = format!("{mem_leaks}\nMemory Leak At Index: {ind}");
                    }
                }
            }
            if !mem_leaks.is_empty() {
                Universal::err_exit(mem_leaks);
            }
            if let Ok(x) = SystemTime::now().duration_since(UNIX_EPOCH) {
                if self.nanosec {
                    println!("Program Took {}ns", x.as_nanos() - start);
                } else {
                    println!("Program Took {}ms", x.as_millis() - start);
                }
            } else {
                println!("Program Took ?~");
            }
            return;
        }
        self.run_commands();
        let mut mem_leaks: String = String::new();
        for i in 0..32 {
            for ratio in 0..8 {
                let ind: usize = i + (ratio * 32);
                if self.mem_ind[ind] != 0 {
                    mem_leaks = format!("{mem_leaks}\nMemory Leak At Index: {ind}");
                }
            }
        }
        if !mem_leaks.is_empty() {
            Universal::err_exit(mem_leaks);
        }
    }
    fn run_commands(&mut self) {
        while self.ptr != self.file_size {
            if self.byte_ind.binary_search(&self.ptr).is_err() {
                self.byte_ind.push(self.ptr);
            }
            let com: u8 = self.next();
            self.run_command(com);
        }
    }
    fn run_commands_with_time(&mut self) {
        while self.ptr != self.file_size {
            let start: u128;
            match SystemTime::now().duration_since(UNIX_EPOCH) {
                Ok(x) => {
                    if self.nanosec {
                        start = x.as_nanos();
                    } else {
                        start = x.as_millis();
                    }
                }
                Err(_) => start = 0,
            }
            if self.byte_ind.binary_search(&self.ptr).is_err() {
                self.byte_ind.push(self.ptr);
            }
            let com: u8 = self.next();
            self.run_command(com);
            if let Ok(x) = SystemTime::now().duration_since(UNIX_EPOCH) {
                if self.nanosec {
                    println!("\nCommand Index {com} Took {}ns", x.as_nanos() - start);
                } else {
                    println!("\nCommand Index {com} Took {}ms", x.as_millis() - start);
                }
            } else {
                println!("\nCommand Index {com} Took ?~");
            }
        }
    }
    fn run_command(&mut self, com: u8) {
        match com {
            0 => self.wvar(),
            1 => {
                let ind: u8 = self.next();
                self.nvar(&ind);
            }
            2 => self.trim(),
            3..=8 => self.math(&com),
            9 => thread::sleep(self.ten_millis),
            10..=13 => self.jump(&com),
            14 => self.print(),
            15 => self.read(),
            16 => self.wfile(),
            17 => self.rfile(),
            18 => self.dfile(),
            _ => Universal::err_exit(format!(
                "\nCommand Index: {com} Is Not Recognized By The Interpreter...\nTerminating...",
            )),
        }
    }

    fn wvar(&mut self) {
        let arg_count: u8 = self.next() - 1;
        let ind: u8 = self.next();
        let resident: &[char] = &self.rvar(&ind);
        let mut out: String = String::new();
        for _ in 0..arg_count {
            let ptr: u8 = self.next();
            if ptr == ind {
                for x in resident {
                    out.push(*x);
                }
            } else {
                for x in self.rvar(&ptr) {
                    out.push(x);
                }
            }
        }
        out = Universal::convert_unicode(&out);
        let mut chars: Chars = out.chars();
        self.write_chars(&ind, &mut chars);
    }
    fn write_chars(&mut self, ind: &u8, chars: &mut Chars) {
        self.nvar(ind);
        let ind_usize: usize = *ind as usize;
        let len: usize = chars.as_str().len();
        for x in 0..len {
            if x + ind_usize == 256 {
                self.mem_ind[ind_usize] = 255;
                return;
            }
            self.mem[x + ind_usize] = chars.next().unwrap();
            if x + ind_usize == 255 {
                self.mem_ind[ind_usize] = 255;
                return;
            }
        }
        self.mem_ind[ind_usize] = ind + (len as u8) - 1;
    }
    fn write_arr(&mut self, ind: &u8, arr: &[char]) {
        self.nvar(ind);
        let ind_usize: usize = *ind as usize;
        let len: usize = arr.len();
        for (x, c) in arr.iter().enumerate() {
            let ptr: usize = x + ind_usize;
            if ptr == 256 {
                self.mem_ind[ind_usize] = 255;
                return;
            }
            self.mem[ptr] = *c;
            if x + ind_usize == 255 {
                self.mem_ind[ind_usize] = 255;
                return;
            }
        }
        self.mem_ind[ind_usize] = ind + (len as u8) - 1;
    }

    fn rvar(&mut self, ind: &u8) -> Vec<char> {
        let ind_usize: usize = *ind as usize;
        if self.mem_ind[ind_usize] == 0 || self.mem_ind[ind_usize] == *ind {
            return vec![self.mem[ind_usize]];
        }
        let mut out: Vec<char> = Vec::<char>::new();
        for x in &self.mem[ind_usize..=self.mem_ind[ind_usize] as usize] {
            out.push(*x);
        }
        out
    }

    fn nvar(&mut self, ind: &u8) {
        let ind_usize: usize = *ind as usize;
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
        let out: f64 = match op {
            3 => num1 + num2,
            4 => num1 - num2,
            5 => num1 * num2,
            6 => num1 / num2,
            7 => num1 % num2,
            8 => ((num1 / num2) as i64) as f64,
            _ => return,
        };
        if out % 1.0 == 0.0 {
            self.write_chars(&ind1, &mut (out as i64).to_string().chars());
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
        None
    }
    fn to_num(arr: &[char]) -> f64 {
        if let Some(x) = Self::find_period(arr) {
            let mut out: [f64; 2] = [0.0; 2];
            for y in 0..x {
                let y2 = x + 1 + y;
                if !(Universal::is_digit(arr[y]) || Universal::is_digit(arr[y2])) {
                    return Self::hash(arr).into();
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
            if !Universal::is_digit(*x) {
                return Self::hash(arr).into();
            }
            out += <u32 as Into<f64>>::into(*x as u32) - 48.0;
            out *= 10.0;
        }
        out / 10.0
    }
    fn hash(arr: &[char]) -> u32 {
        let mut hash: u32 = 0;
        for x in arr {
            hash = 31 * hash + (*x as u32);
        }
        hash
    }

    fn jump(&mut self, op: &u8) {
        let ind1: u8 = self.next();
        let ind2: u8 = self.next();
        let val1: Vec<char> = self.rvar(&ind1);
        let val2: Vec<char> = self.rvar(&ind2);
        let com: u16 = self.next_u16();
        if (op == &10 && Self::to_num(&val1) > Self::to_num(&val2))
            || (op == &11 && Self::to_num(&val1) < Self::to_num(&val2))
            || (op == &12 && val1.eq(&val2))
            || (op == &13 && !val1.eq(&val2))
        {
            if com < self.byte_ind.len() as u16 {
                self.ptr = self.byte_ind[com as usize];
                return;
            }
            self.skip(&com);
        }
    }
    fn skip(&mut self, com: &u16) {
        while com != &(self.byte_ind.len() as u16) && self.ptr < self.file_size {
            self.byte_ind.push(self.ptr);
            let cur: u8 = self.next();
            match cur {
                0 => self.ptr += self.next() as u64,
                1 => self.ptr += 1,
                2..=8 => self.ptr += 2,
                9 => (),
                10..=13 => self.ptr += 4,
                14 => self.ptr += self.next() as u64,
                15 => self.ptr += 1,
                16 => self.ptr += self.next() as u64,
                17 => self.ptr += self.next() as u64,
                18 => self.ptr += self.next() as u64,
                // TODO: Add Other Commands
                _ => panic!("You Forgot To Add Command Number {cur} To The Skip Index..."),
            }
        }
    }

    fn print(&mut self) {
        let arg_count: u8 = self.next();
        let mut out: String = String::new();
        for _ in 0..arg_count as usize {
            let ind: u8 = self.next();
            for x in self.rvar(&ind) {
                out.push(x);
            }
        }
        print!("{}", Universal::convert_unicode(&out));
        if let Err(x) = io::stdout().flush() {
            Universal::err_exit(x.to_string());
        }
    }

    fn read(&mut self) {
        print!("=>");
        if let Err(x) = io::stdout().flush() {
            Universal::err_exit(x.to_string());
        }
        let stdin: Stdin = io::stdin();
        let mut buf: String = String::new();
        if let Err(x) = stdin.read_line(&mut buf) {
            Universal::err_exit(x.to_string());
        }
        let ind: u8 = self.next();
        self.write_chars(&ind, &mut buf.chars());
    }

    fn get_file_name(&mut self, arg_count: usize) -> String {
        let mut out: String = String::new();
        for _ in 0..arg_count {
            let ind: u8 = self.next();
            for x in self.rvar(&ind) {
                out.push(x);
            }
        }
        out = Universal::convert_unicode(&out);
        if Path::new(&out).is_relative() {
            if let Some(x) = Path::new(&self.file_name)
                .canonicalize()
                .unwrap()
                .as_path()
                .parent()
            {
                out = format!("{}/{out}", x.display());
            }
        }
        out
    }
    fn wfile(&mut self) {
        let arg_count: u8 = self.next() - 1;
        let ind: u8 = self.next();
        let file_name: String = self.get_file_name(arg_count as usize);
        let mut out: String = String::new();
        for x in self.rvar(&ind) {
            out.push(x);
        }
        match File::open(&file_name) {
            Err(x) => {
                if x.kind() == ErrorKind::PermissionDenied {
                    Universal::err_exit(x.to_string());
                }
                match File::create(&file_name) {
                    Err(x) => match x.kind() {
                        ErrorKind::PermissionDenied => Universal::err_exit(x.to_string()),
                        ErrorKind::NotFound => {
                            if let Some(x) = Path::new(&file_name).parent() {
                                if let Err(x) = fs::create_dir_all(x) {
                                    Universal::err_exit(x.to_string());
                                }
                            }
                            if let Err(x) = File::create(&file_name).unwrap().write(out.as_bytes())
                            {
                                Universal::err_exit(x.to_string());
                            }
                        }
                        _ => Universal::err_exit(x.to_string()),
                    },
                    Ok(mut x) => {
                        if let Err(x) = x.write(out.as_bytes()) {
                            Universal::err_exit(x.to_string());
                        }
                    }
                }
            }
            Ok(_) => match File::create(&file_name) {
                Ok(mut x) => {
                    if let Err(x) = x.write(out.as_bytes()) {
                        Universal::err_exit(x.to_string());
                    }
                }
                Err(x) => Universal::err_exit(x.to_string()),
            },
        }
    }

    fn rfile(&mut self) {
        let arg_count: u8 = self.next() - 1;
        let ind: u8 = self.next();
        let file_name: String = self.get_file_name(arg_count as usize);
        let out: String = match fs::read_to_string(file_name) {
            Ok(x) => x,
            Err(x) => {
                Universal::err_exit(format!(
                    "File Provided Does Not Exist...\n{x}\nTerminating..."
                ));
                String::new()
            }
        };
        self.write_chars(&ind, &mut out.chars());
    }

    fn dfile(&mut self) {
        let arg_count: u8 = self.next();
        let file_name: String = self.get_file_name(arg_count as usize);
        if let Err(x) = fs::remove_dir_all(file_name) {
            Universal::err_exit(format!(
                "File Provided Does Not Exist...\n{x}\nTerminating..."
            ));
        }
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
        buf[0]
    }
    fn next_u16(&mut self) -> u16 {
        let mut buf: [u8; 2] = [0; 2];
        if let Err(x) = self.file.seek(Start(self.ptr)) {
            Universal::err_exit(x.to_string());
        }
        if let Err(x) = self.file.read_exact(&mut buf) {
            Universal::err_exit(x.to_string())
        }
        self.ptr += 2;
        ((buf[0] as u16) << 8) | (buf[1] as u16)
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
        mem
    }
}
