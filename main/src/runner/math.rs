use crate::runner::Runner;
use crate::runner::Variables;
use crate::Universal;

pub trait Math {
    fn add(&mut self);
    fn sub(&mut self);
    fn mul(&mut self);
    fn div(&mut self);
    fn r#mod(&mut self);
    fn rmod(&mut self);
    fn get_math_vals(&mut self) -> (u8, f64, f64);
    fn write_math_res(&mut self, ind: &u8, res: f64);
    fn check_math_div_err(num2: &f64) -> bool;
    fn find_period(arr: &[char]) -> Option<usize>;
    fn to_num(arr: &[char]) -> f64;
    fn hash(arr: &[char]) -> u32;
}

impl Math for Runner {
    fn add(&mut self) {
        let (ind, num1, num2): (u8, f64, f64) = self.get_math_vals();
        self.write_math_res(&ind, num1 + num2);
    }
    fn sub(&mut self) {
        let (ind, num1, num2): (u8, f64, f64) = self.get_math_vals();
        self.write_math_res(&ind, num1 - num2);
    }
    fn mul(&mut self) {
        let (ind, num1, num2): (u8, f64, f64) = self.get_math_vals();
        self.write_math_res(&ind, num1 * num2);
    }
    fn div(&mut self) {
        let (ind, num1, num2): (u8, f64, f64) = self.get_math_vals();
        if Self::check_math_div_err(&num2) {
            self.write_arr(&ind, &['i']);
            return;
        }
        self.write_math_res(&ind, num1 / num2);
    }
    fn r#mod(&mut self) {
        let (ind, num1, num2): (u8, f64, f64) = self.get_math_vals();
        if Self::check_math_div_err(&num2) {
            self.write_arr(&ind, &['i']);
            return;
        }
        self.write_math_res(&ind, num1 % num2);
    }
    fn rmod(&mut self) {
        let (ind, num1, num2): (u8, f64, f64) = self.get_math_vals();
        if Self::check_math_div_err(&num2) {
            self.write_arr(&ind, &['i']);
            return;
        }
        self.write_math_res(&ind, ((num1 / num2) as i64) as f64);
    }

    fn get_math_vals(&mut self) -> (u8, f64, f64) {
        let ind1: u8 = self.next();
        let ind2: u8 = self.next();
        (
            ind1,
            Self::to_num(&self.rvar(&ind1)),
            Self::to_num(&self.rvar(&ind2)),
        )
    }
    fn write_math_res(&mut self, ind: &u8, res: f64) {
        if res % 1.0 == 0.0 {
            self.write_chars(ind, &mut (res as i64).to_string().chars());
        } else {
            self.write_chars(ind, &mut res.to_string().chars());
        }
    }
    fn check_math_div_err(num2: &f64) -> bool {
        if num2 == &0.0 {
            return true;
        }
        false
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
        arr.iter().for_each(|x| {
            hash = 31 * hash + (*x as u32);
        });
        hash
    }
}
