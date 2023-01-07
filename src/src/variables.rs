use crate::runner::Runner;
use crate::universal::Universal;

use std::str::Chars;

pub trait Variables {
    fn wvar(&mut self);
    fn write_chars(&mut self, ind: &u8, chars: &mut Chars);
    fn write_arr(&mut self, ind: &u8, arr: &[char]);
    fn nvar(&mut self);
    fn rvar(&mut self, ind: &u8) -> Vec<char>;
    fn nullify(&mut self, ind: &u8);
    fn trim(&mut self);
}

impl Variables for Runner {
    fn wvar(&mut self) {
        let arg_count: u8 = self.next() - 1;
        let ind: u8 = self.next();
        let resident: String = String::from_iter(self.rvar(&ind));
        let mut out: String = String::new();
        (0..arg_count).into_iter().for_each(|_| {
            let ptr: u8 = self.next();
            out.push_str(
                &(if ptr == ind {
                    resident.clone()
                } else {
                    String::from_iter(self.rvar(&ptr))
                }),
            );
        });
        self.write_chars(&ind, &mut Universal::convert_unicode(&out).chars());
    }
    fn write_chars(&mut self, ind: &u8, chars: &mut Chars) {
        self.write_arr(ind, &chars.collect::<Vec<char>>());
    }
    fn write_arr(&mut self, ind: &u8, arr: &[char]) {
        self.nullify(ind);
        let ind_usize: usize = *ind as usize;
        let len: usize = arr.len();
        for (x, c) in arr.iter().enumerate() {
            let ptr: usize = x + ind_usize;
            if ptr == 256 {
                self.runner_data.mem_ind[ind_usize] = 255;
                return;
            }
            self.runner_data.mem[ptr] = *c;
            if x + ind_usize == 255 {
                self.runner_data.mem_ind[ind_usize] = 255;
                return;
            }
        }
        self.runner_data.mem_ind[ind_usize] = ind + (len as u8) - 1;
    }
    fn nvar(&mut self) {
        let ind: u8 = self.next();
        self.nullify(&ind);
    }
    fn rvar(&mut self, ind: &u8) -> Vec<char> {
        let ind_usize: usize = *ind as usize;
        if self.runner_data.mem_ind[ind_usize] == 0 {
            return self.runner_data.mem[ind_usize..=ind_usize].to_vec();
        }
        self.runner_data.mem[ind_usize..=self.runner_data.mem_ind[ind_usize] as usize].to_vec()
    }
    fn nullify(&mut self, ind: &u8) {
        let ind_usize: usize = *ind as usize;
        if self.runner_data.mem_ind[ind_usize] == 0 {
            return;
        }
        (ind_usize..=self.runner_data.mem_ind[ind_usize] as usize)
            .into_iter()
            .for_each(|x| {
                self.runner_data.mem[x] = '\u{0000}';
            });
        self.runner_data.mem_ind[ind_usize] = 0;
    }
    fn trim(&mut self) {
        let ind: u8 = self.next();
        let trim_size: u8 = self.next();
        if trim_size == 0 {
            self.nullify(&ind);
            return;
        }
        let resident: Vec<char> = self.rvar(&ind);
        if trim_size as usize >= resident.len() {
            return;
        }
        self.write_arr(&ind, &resident[0..trim_size as usize]);
    }
}
