use crate::math::Math;
use crate::universal::Universal;
use crate::variables::Variables;

use std::collections::HashMap;
use std::fs::{self, File};
use std::io::{self, ErrorKind, Read, Seek, SeekFrom::Start, Write};
use std::path::Path;
use std::thread;
use std::time::{Duration, SystemTime, UNIX_EPOCH};

pub struct FileMeta {
    pub file: File,
    pub file_name: String,
    pub file_size: u64,
}

pub struct RunnerData {
    pub ptr: u64,
    pub mem_ind: [u8; 256],
    pub mem: [char; 256],
    pub byte_ind: Vec<u64>,
}
pub struct Runner {
    pub file_meta: FileMeta,
    pub runner_data: RunnerData,
    pub runner_data_copy: Vec<RunnerData>,
    pub funcs: HashMap<String, u64>,
    pub ten_millis: Duration,
    pub comms: Vec<fn(&mut Self) -> ()>,
    perfmes: bool,
    nanosec: bool,
    commmes: bool,
}

impl Runner {
    pub fn new(file_name: String, perfmes: bool, nanosec: bool, commmes: bool) -> Runner {
        let file: File = File::open(&file_name).unwrap_or_else(|x| {
            Universal::err_exit(x.to_string());
            File::open("").unwrap()
        });
        let file_size: u64 = file
            .metadata()
            .unwrap_or_else(|x| {
                Universal::err_exit(x.to_string());
                file.metadata().unwrap()
            })
            .len();
        Runner {
            file_meta: FileMeta {
                file,
                file_name,
                file_size,
            },
            runner_data: RunnerData {
                ptr: 0,
                mem_ind: [0; 256],
                mem: Self::init_mem(),
                byte_ind: Vec::<u64>::new(),
            },
            runner_data_copy: Vec::<RunnerData>::new(),
            funcs: HashMap::<String, u64>::new(),
            ten_millis: Duration::from_millis(10),
            comms: vec![
                Self::wvar,
                Self::nvar,
                Self::trim,
                Self::add,
                Self::sub,
                Self::mul,
                Self::div,
                Self::r#mod,
                Self::rmod,
                Self::nop,
                Self::jm,
                Self::jl,
                Self::je,
                Self::jne,
                Self::print,
                Self::read,
                Self::wfile,
                Self::rfile,
                Self::dfile,
                Self::wfunc,
                Self::cfunc,
                Self::ufunc,
                Self::wvar,
            ],
            perfmes,
            nanosec,
            commmes,
        }
    }

    pub fn run(&mut self) {
        if self.perfmes {
            let res_time: Result<Duration, _> = SystemTime::now().duration_since(UNIX_EPOCH);
            let start: u128;
            if let Ok(x) = res_time {
                if self.nanosec {
                    start = x.as_nanos();
                } else {
                    start = x.as_millis();
                }
            } else {
                start = 0;
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
                    if self.runner_data.mem_ind[ind] != 0 {
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
                if self.runner_data.mem_ind[ind] != 0 {
                    mem_leaks.push_str(&format!("\nMemory Leak At Index: {ind}"));
                }
            }
        }
        if !mem_leaks.is_empty() {
            Universal::err_exit(mem_leaks);
        }
    }
    fn run_commands(&mut self) {
        while self.runner_data.ptr != self.file_meta.file_size {
            if self
                .runner_data
                .byte_ind
                .binary_search(&self.runner_data.ptr)
                .is_err()
            {
                self.runner_data.byte_ind.push(self.runner_data.ptr);
            }
            let com: u8 = self.next();
            self.run_command(com);
        }
    }
    fn run_commands_with_time(&mut self) {
        while self.runner_data.ptr != self.file_meta.file_size {
            let start: u128;
            if let Ok(x) = SystemTime::now().duration_since(UNIX_EPOCH) {
                if self.nanosec {
                    start = x.as_nanos();
                } else {
                    start = x.as_millis();
                }
            } else {
                start = 0;
            }
            if self
                .runner_data
                .byte_ind
                .binary_search(&self.runner_data.ptr)
                .is_err()
            {
                self.runner_data.byte_ind.push(self.runner_data.ptr);
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
        if com as usize > self.comms.len() - 1 {
            Universal::err_exit(format!(
                "\nCommand Index: {com} Is Not Recognized By The Interpreter...\nTerminating...",
            ));
        }
        self.comms[com as usize](self);
    }

    fn nop(&mut self) {
        thread::sleep(self.ten_millis);
    }

    fn jm(&mut self) {
        let (val1, val2, com): (f64, f64, u16) = self.get_jump_vals2();
        if val1 > val2 {
            self.jump_to_com(com);
        }
    }
    fn jl(&mut self) {
        let (val1, val2, com): (f64, f64, u16) = self.get_jump_vals2();
        if val1 < val2 {
            self.jump_to_com(com);
        }
    }
    fn je(&mut self) {
        let (val1, val2, com): (Vec<char>, Vec<char>, u16) = self.get_jump_vals();
        if val1.eq(&val2) {
            self.jump_to_com(com);
        }
    }
    fn jne(&mut self) {
        let (val1, val2, com): (Vec<char>, Vec<char>, u16) = self.get_jump_vals();
        if val1.ne(&val2) {
            self.jump_to_com(com);
        }
    }

    fn get_jump_vals(&mut self) -> (Vec<char>, Vec<char>, u16) {
        let ind1: u8 = self.next();
        let ind2: u8 = self.next();
        let val1: Vec<char> = self.rvar(&ind1);
        let val2: Vec<char> = self.rvar(&ind2);
        let com: u16 = self.next_u16();
        (val1, val2, com)
    }
    fn get_jump_vals2(&mut self) -> (f64, f64, u16) {
        let (val1, val2, com): (Vec<char>, Vec<char>, u16) = self.get_jump_vals();
        (Self::to_num(&val1), Self::to_num(&val2), com)
    }
    fn jump_to_com(&mut self, com: u16) {
        if com < self.runner_data.byte_ind.len() as u16 {
            self.runner_data.ptr = self.runner_data.byte_ind[com as usize];
            return;
        }
        self.skip(&com);
    }
    fn skip(&mut self, com: &u16) {
        if com == &(self.runner_data.byte_ind.len() as u16)
            || self.runner_data.ptr >= self.file_meta.file_size
        {
            return;
        }
        self.runner_data.byte_ind.push(self.runner_data.ptr);
        let cur: u8 = self.next();
        self.ptr_skip(cur);
        self.skip(com);
    }
    fn ptr_skip(&mut self, com: u8) {
        match com {
            1 | 15 => self.runner_data.ptr += 1,
            2..=8 => self.runner_data.ptr += 2,
            9 => (),
            10..=13 => self.runner_data.ptr += 4,
            0 | 14 | 16..=18 => self.runner_data.ptr += self.next() as u64,
            // TODO: Add Other Commands
            _ => panic!("You Forgot To Add Command Number {com} To The Skip Index..."),
        }
    }

    fn print(&mut self) {
        let arg_count: u8 = self.next();
        print!("{}", self.get_args(arg_count as usize, true));
        if let Err(x) = io::stdout().flush() {
            Universal::err_exit(x.to_string());
        }
    }

    fn read(&mut self) {
        print!("=>");
        if let Err(x) = io::stdout().flush() {
            Universal::err_exit(x.to_string());
        }
        let mut buf: String = String::new();
        if let Err(x) = io::stdin().read_line(&mut buf) {
            Universal::err_exit(x.to_string());
        }
        let ind: u8 = self.next();
        self.write_chars(&ind, &mut buf.chars());
    }

    fn get_indexes(&mut self, arg_count: usize) -> Vec<u8> {
        let mut out: Vec<u8> = Vec::<u8>::new();
        for _ in 0..arg_count {
            out.push(self.next());
        }
        out
    }
    fn get_args(&mut self, arg_count: usize, convert_unicode: bool) -> String {
        let mut out: String = String::new();
        for x in self.get_indexes(arg_count) {
            for y in self.rvar(&x) {
                out.push(y);
            }
        }
        if convert_unicode {
            return Universal::convert_unicode(&out);
        }
        out
    }
    fn get_file_name(&mut self, arg_count: u8) -> String {
        let out: String = self.get_args(arg_count as usize, true);
        if Path::new(&out).is_relative() {
            if let Some(x) = Path::new(&self.file_meta.file_name)
                .canonicalize()
                .unwrap()
                .as_path()
                .parent()
            {
                return format!("{}/{out}", x.display());
            }
        }
        out
    }
    fn wfile(&mut self) {
        let arg_count: u8 = self.next() - 1;
        let ind: u8 = self.next();
        let file_name: String = self.get_file_name(arg_count);
        let out: String = self.rvar(&ind).iter().cloned().collect::<String>();
        if let Err(x) = File::create(&file_name)
            .unwrap_or_else(|x| {
                if x.kind() != ErrorKind::NotFound {
                    Universal::err_exit(x.to_string());
                }
                if let Some(x) = Path::new(&file_name).parent() {
                    if let Err(x) = fs::create_dir_all(x) {
                        Universal::err_exit(x.to_string());
                    }
                }
                File::create(&file_name).unwrap_or_else(|x| {
                    Universal::err_exit(x.to_string());
                    File::open("").unwrap()
                })
            })
            .write(out.as_bytes())
        {
            Universal::err_exit(x.to_string());
        }
    }

    fn rfile(&mut self) {
        let arg_count: u8 = self.next() - 1;
        let ind: u8 = self.next();
        let file_name: String = self.get_file_name(arg_count);
        self.write_chars(
            &ind,
            &mut fs::read_to_string(file_name)
                .unwrap_or_else(|x| {
                    Universal::err_exit(x.to_string());
                    String::new()
                })
                .chars(),
        );
    }

    fn dfile(&mut self) {
        let arg_count: u8 = self.next();
        let file_name: String = self.get_file_name(arg_count);
        if let Err(x) = fs::remove_dir_all(file_name) {
            Universal::err_exit(x.to_string());
        }
    }

    fn wfunc(&mut self) {
        let ptr: u64 = self.runner_data.ptr;
        let arg_count: u16 = self.next_u16();
        let func_name: String = self.get_args(arg_count as usize, false);
        let arg_count2: u8 = self.next();
        #[allow(unused_variables)]
        let func_args: Vec<u8> = self.get_indexes(arg_count2 as usize);
        self.funcs.insert(func_name.clone(), ptr);
        loop {
            if self.runner_data.ptr >= self.file_meta.file_size {
                return;
            }
            let com: u8 = self.next();
            if com == 20 {
                let _this_arg_count: u16 = self.next_u16();
                let this_func_name: String = self.get_args(arg_count as usize, false);
                if this_func_name.eq(&func_name) {
                    break;
                }
            } else {
                self.ptr_skip(com);
            }
        }
    }

    fn cfunc(&mut self) {
        let arg_count: u16 = self.next_u16();
        let func_name: String = self.get_args(arg_count as usize, false);
        if self.funcs.get(&func_name).is_some() {
            self.runner_data = self.runner_data_copy.pop().unwrap();
        }
    }

    fn ufunc(&mut self) {
        let arg_count: u16 = self.next_u16();
        let func_name: String = self.get_args(arg_count as usize, false);
        let given_arg_count: u8 = self.next();
        let _given_args: Vec<u8> = self.get_indexes(given_arg_count as usize);
        if let Some(x) = self.funcs.get(&func_name) {
            self.runner_data_copy.push(RunnerData {
                ptr: self.runner_data.ptr,
                mem: self.runner_data.mem,
                mem_ind: self.runner_data.mem_ind,
                byte_ind: self.runner_data.byte_ind.clone(),
            });
            self.runner_data = RunnerData {
                ptr: *x + 2 + arg_count as u64,
                mem: Self::init_mem(),
                mem_ind: [0; 256],
                byte_ind: Vec::<u64>::new(),
            };
            let arg_count2: u8 = self.next();
            let _func_args: Vec<u8> = self.get_indexes(arg_count2 as usize);
        }
    }

    pub fn next(&mut self) -> u8 {
        let mut buf: [u8; 1] = [0; 1];
        if let Err(x) = self.file_meta.file.seek(Start(self.runner_data.ptr)) {
            Universal::err_exit(x.to_string());
        }
        if let Err(x) = self.file_meta.file.read_exact(&mut buf) {
            Universal::err_exit(x.to_string())
        }
        self.runner_data.ptr += 1;
        buf[0]
    }
    pub fn next_u16(&mut self) -> u16 {
        ((self.next() as u16) << 8) | (self.next() as u16)
    }

    pub fn init_mem() -> [char; 256] {
        let rom: [char; 38] = [
            ' ', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', '\n',
        ];
        core::array::from_fn(|i| match i {
            0..=37 => rom[i],
            _ => '\0',
        })
    }
}
