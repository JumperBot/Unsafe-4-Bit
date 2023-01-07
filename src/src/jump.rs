use crate::math::Math;
use crate::runner::Runner;
use crate::variables::Variables;

pub trait Jump {
    fn jm(&mut self);
    fn jl(&mut self);
    fn je(&mut self);
    fn jne(&mut self);
    fn get_jump_vals(&mut self) -> (Vec<char>, Vec<char>, u16);
    fn get_jump_vals2(&mut self) -> (f64, f64, u16);
    fn jump_to_com(&mut self, com: u16);
    fn skip(&mut self, com: &u16);
    fn ptr_skip(&mut self, com: u8);
}

impl Jump for Runner {
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
}
