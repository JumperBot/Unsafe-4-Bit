pub struct MemoryMap {
    keys: Vec<String>,
    mems: Vec<u64>,
}

impl MemoryMap {
    pub fn new_limited() -> MemoryMap {
        MemoryMap {
            keys: vec![
                " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9", "\n",
            ]
            .into_iter()
            .map(|x| x.to_string())
            .collect::<Vec<String>>(),
            mems: (0..38).collect::<Vec<u64>>(),
        }
    }
    pub fn new_binary_map() -> MemoryMap {
        let keys: Vec<String> = vec![
            "wvar", "nvar", "trim", "add", "sub", "mul", "div", "mod", "rmod", "nop", "jm", "jl",
            "je", "jne", "print", "read", "wfile", "rfile", "dfile", "wfunc", "cfunc", "ufunc",
        ]
        .into_iter()
        .map(|x| x.to_string())
        .collect::<Vec<String>>();
        MemoryMap {
            keys: keys.clone(),
            mems: (0..keys.len())
                .map(|x| x.try_into().unwrap())
                .collect::<Vec<u64>>(),
        }
    }

    fn get_index(keys: &[String], key: &str) -> Result<usize, ()> {
        for (x, item) in keys.iter().enumerate() {
            if item.eq(key) {
                return Ok(x);
            }
        }
        Err(())
    }
    pub fn get(&self, key: &str) -> u64 {
        if let Ok(x) = Self::get_index(&self.keys, key) {
            return *self.mems.get(x).unwrap();
        }
        0
    }
    pub fn contains_key(&self, key: &str) -> bool {
        Self::get_index(&self.keys, key).is_ok()
    }
}
