/**
 *
 *	Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.
 *	Copyright (C) 2022  JumperBot_
 *
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
**/

pub struct MemoryMap {
    keys: Vec<String>,
    mems: Vec<u64>,
}

impl MemoryMap {
    pub fn new_limited() -> MemoryMap {
        return MemoryMap {
            keys: vec![
                " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9", "\n",
            ]
            .into_iter()
            .map(|x| x.to_string())
            .collect::<Vec<String>>(),
            mems: (0..38).collect::<Vec<u64>>(),
        };
    }
    pub fn new_binary_map() -> MemoryMap {
        return MemoryMap {
            keys: vec![
                "wvar", "nvar", "trim", "add", "sub", "mul", "div", "mod", "rmod", "nop", "jm",
                "jl", "je", "jne", "print", "read", "wfile", "rfile", "dfile",
            ]
            .into_iter()
            .map(|x| x.to_string())
            .collect::<Vec<String>>(),
            mems: (0..19).collect::<Vec<u64>>(),
        };
    }

    fn get_index(keys: &Vec<String>, key: &str) -> Result<usize, ()> {
        for (x, item) in keys.iter().enumerate() {
            if item.eq(key) {
                return Ok(x);
            }
        }
        return Err(());
    }
    pub fn get(&self, key: &str) -> u64 {
        match Self::get_index(&self.keys, key) {
            Ok(x) => return self.mems.get(x).unwrap().clone(),
            Err(_) => return 0,
        };
    }
    pub fn contains_key(&self, key: &str) -> bool {
        match Self::get_index(&self.keys, key) {
            Ok(_) => return true,
            Err(_) => return false,
        };
    }
}
