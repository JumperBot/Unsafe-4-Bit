use crate::runner::Runner;
use crate::universal::Universal;
use crate::variables::Variables;

use std::fs::{self, File};
use std::io::{ErrorKind, Write};
use std::path::Path;

pub trait FileIO {
    fn get_file_name(&mut self, arg_count: u8) -> String;
    fn wfile(&mut self);
    fn rfile(&mut self);
    fn dfile(&mut self);
}

impl FileIO for Runner {
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
}
