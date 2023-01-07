use crate::jump::Jump;
use crate::runner::Runner;
use crate::runner::RunnerData;

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
}
