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

pub struct MemoryMap{
    pub keys: Vec<String>,
    pub mems: Vec<u64>
}

impl MemoryMap{
    pub fn new() -> MemoryMap{
        return MemoryMap{
            keys: Vec::<String>::new(),
            mems: Vec::<u64>::new(),
        };
    }
    
    pub fn put(&mut self, key: String, mem: u64){
        self.keys.push(key.clone());
        self.mems.push(mem.clone());
    }
    
    fn get_index<'a>(keys: &'a Vec<String>, key: &'a str) -> Result<usize, &'a str>{
        for x in 0..keys.len(){
            if keys.get(x).unwrap().eq(key){
                return Ok(x);
            }
        }
        return Err("Key Does Not Exist In The Map.");
    }
    pub fn get(&self, key: &str) -> u64{
        match Self::get_index(&self.keys, key){
            Ok(x)  => return self.mems.get(x).unwrap().clone(),
            Err(x) => return 0,
        };
    }

    pub fn contains_key(&self, key: &str) -> bool{
        match(Self::get_index(&self.keys, key)){
            Ok(x)  => return true,
            Err(x) => return false,
        };
    }
}
