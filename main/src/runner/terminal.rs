use crate::runner::Runner;
use crate::runner::Universal;
use crate::runner::Variables;

use std::io::{self, Write};
use std::thread;

pub trait Terminal {
    fn nop(&mut self);
    fn print(&mut self);
    fn read(&mut self);
}

impl Terminal for Runner {
    fn nop(&mut self) {
        thread::sleep(self.ten_millis);
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
}
