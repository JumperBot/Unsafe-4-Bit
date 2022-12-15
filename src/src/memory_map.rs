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

//use crate::universal::Universal;

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
    
    pub fn put(&mut self, key: &str, mem: &u64){
        self.keys.push(key.to_string());
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
    fn get_index2<'a>(mems: &'a Vec<u64>, mem: &'a u64) -> Result<usize, &'a str>{
        for x in 0..mems.len(){
            if mems.get(x).unwrap()==mem{
                return Ok(x);
            }
        }
        return Err("Mem Does Not Exist In The Map.");
    }
    pub fn get(&self, key: &str) -> u64{
        match Self::get_index(&self.keys, key){
            Ok(x)  => return self.mems.get(x).unwrap().clone(),
            Err(_x) => return 0,
        };
    }

    pub fn contains_key(&self, key: &str) -> bool{
        match Self::get_index(&self.keys, key){
            Ok(_x)  => return true,
            Err(_x) => return false,
        };
    }

    pub fn remove_mem_if_exists(&mut self, mem: &u64){
        let ind: usize=match Self::get_index2(&self.mems, &mem){
            Ok(x)  => x,
            Err(_x) => {
                return ();
            }
        };
        self.keys.remove(ind);
        self.mems.remove(ind);
    }

    /*
    pub fn to_string(&self) -> String{
        return format!(
            "{}\n{}",
            Universal::arr_to_string(&self.keys),
            Universal::arr_to_string(&self.mems)
        );
    }
    */
}
