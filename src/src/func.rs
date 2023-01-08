use crate::jump::Jump;
use crate::runner::Runner;
use crate::runner::RunnerData;
use crate::variables::Variables;

pub trait Func {
    fn wfunc(&mut self);
    fn cfunc(&mut self);
    fn ufunc(&mut self);
}

impl Func for Runner {
    fn wfunc(&mut self) {
        let ptr: u64 = self.runner_data.ptr;
        let arg_count: u16 = self.next_u16();
        let func_name: String = self.get_args(arg_count as usize, false);
        self.runner_data.ptr += self.next() as u64;
        self.funcs.insert(func_name.clone(), ptr);
        while self.runner_data.ptr >= self.file_meta.file_size {
            let command: u8 = self.next();
            if command == 20 {
                let this_func_name_len: u16 = self.next_u16();
                let this_func_name: String = self.get_args(this_func_name_len as usize, false);
                if this_func_name.eq(&func_name) {
                    break;
                }
            } else {
                self.ptr_skip(command);
            }
        }
    }

    fn cfunc(&mut self) {
        let arg_count: u16 = self.next_u16();
        let func_name: String = self.get_args(arg_count as usize, false);
        if self.funcs.get(&func_name).is_some() {
            if let Some(x) = self.runner_data_copy.pop() {
                self.runner_data = x;
            }
        }
    }

    fn ufunc(&mut self) {
        let arg_count: u16 = self.next_u16();
        let func_name: String = self.get_args(arg_count as usize, false);
        let given_arg_count: usize = self.next() as usize;
        let mut given_args: Vec<Vec<char>> = Vec::<Vec<char>>::with_capacity(given_arg_count);
        self.get_indexes(given_arg_count).iter().for_each(|x| {
            given_args.push(self.rvar(x));
        });
        if let Some(x) = self.funcs.get(&func_name) {
            self.runner_data_copy.push(self.runner_data.clone());
            self.runner_data = RunnerData::new();
            self.runner_data.ptr = *x + 2 + arg_count as u64;
            let arg_count2: u8 = self.next();
            let func_args: Vec<u8> = self.get_indexes(arg_count2 as usize);
            while given_args.len() < func_args.len() {
                given_args.push(vec!['\0']);
            }
            while given_args.len() > func_args.len() {
                given_args.pop();
            }
            func_args.iter().enumerate().for_each(|(x, y)| {
                self.write_arr(y, &given_args[x]);
            });
        }
    }
}
