use crate::fileio::FileIO;
use crate::func::Func;
use crate::jump::Jump;
use crate::math::Math;
use crate::terminal::Terminal;
use crate::universal::Universal;
use crate::variables::Variables;

use std::collections::HashMap;
use std::fs::File;
use std::io::{Read, Seek, SeekFrom::Start};
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
    pub fn run_commands(&mut self) {
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
    pub fn run_commands_with_time(&mut self) {
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
    pub fn run_command(&mut self, com: u8) {
        if com as usize > self.comms.len() - 1 {
            Universal::err_exit(format!(
                "\nCommand Index: {com} Is Not Recognized By The Interpreter...\nTerminating..."
            ));
        }
        self.comms[com as usize](self);
    }

    pub fn get_indexes(&mut self, arg_count: usize) -> Vec<u8> {
        let mut out: Vec<u8> = Vec::<u8>::new();
        for _ in 0..arg_count {
            out.push(self.next());
        }
        out
    }
    pub fn get_args(&mut self, arg_count: usize, convert_unicode: bool) -> String {
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
