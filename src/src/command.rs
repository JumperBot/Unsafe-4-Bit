use std::collections::HashMap;

use crate::memory_map::MemoryMap;
use crate::universal::Universal;

pub struct Command {}

impl Command {
    /*
     * 0    -	0000	-	wvar	|	1	-	0001	-	nvar
     * 2	-	0010	-	trim	|	3	-	0011	-	add
     * 4	-	0100	-	sub		|   5   -	0101	-	mul
     * 6	-	0110	-	div		|   7	-	0111	-	mod
     * 8	-	1000	-	rmod	|	9	-	1001	-	nop
     * 10	-	1010	-	jm		|	11	-	1011    -   jl
     * 12	-	1100	-	je		|	13	-	1101	-	jne
     * 14	-	1110	-	print	|	15	-	1111	-	read
    	**/
    /*
     * 16   -   00010000    -   wfile
     * 17   -   00010001    -   rfile
     * 18   -   00010010    -   dfile
     * 19   -   00010011    -   wfunc
     * 20   -   00010100    -   cfunc
     * 21   -   00010101    -   ufunc
     **/
    pub fn new(
        line: &[String],
        real_line: &[String],
        binary_map: &MemoryMap,
    ) -> Result<Vec<u8>, String> {
        let com_str: String = line[0].to_lowercase();
        if !binary_map.contains_key(&com_str) {
            return Err(UnrecognizedCommand::create(real_line, line).analyze());
        }
        let ind: u64 = binary_map.get(&com_str);
        let command: Box<dyn GenericCommand> = match ind {
            0 => WvarCommand::create(real_line, line),
            1 => NvarCommand::create(real_line, line),
            2 => TrimCommand::create(real_line, line),
            3..=8 => MathCommand::create(real_line, line),
            9 => NopCommand::create(real_line, line),
            10..=13 => JumpCommand::create(real_line, line),
            14 => PrintCommand::create(real_line, line),
            15 => ReadCommand::create(real_line, line),
            16 => WfileCommand::create(real_line, line),
            17 => RfileCommand::create(real_line, line),
            18 => DfileCommand::create(real_line, line),
            19 => WfuncCommand::create(real_line, line),
            20 => CfuncCommand::create(real_line, line),
            21 => UfuncCommand::create(real_line, line),
            _ => UnrecognizedCommand::create(real_line, line),
        };
        let err: String = command.analyze();
        if !err.is_empty() {
            return Err(err);
        }
        Ok(command.compile())
    }

    pub fn check_arg_length_using_limit(
        real_line: &[String],
        line: &[String],
        limit: usize,
    ) -> String {
        if line.len() - 1 > limit {
            return Universal::format_error(
                real_line,
                &[
                    "Command",
                    &line[0],
                    &format!("Has More Than The Maximum {limit} Allowed Arguments"),
                ],
            );
        }
        String::new()
    }
    pub fn check_arg_length(real_line: &[String], line: &[String], len: usize) -> String {
        let num: &str = match len {
            0 => "Zero",
            1 => "One",
            2 => "Two",
            _ => "Three",
        };
        if line.len() != len + 1 {
            return Universal::format_error(
                real_line,
                &[
                    "Command",
                    &line[0],
                    &format!("Needs No Less And No More Than {num} Arguments To Work"),
                ],
            );
        }
        String::new()
    }
    pub fn check_if_mem_ind(real_line: &[String], ind: String) -> String {
        if let Ok(x) = ind.parse::<u64>() {
            if x > 255 {
                return Universal::format_error(
                    real_line,
                    &[
                        "Memory Index",
                        &ind,
                        "Is Larger Than 255 And Will Not Point To Memory",
                    ],
                );
            }
            return String::new();
        }
        Universal::format_error(
            real_line,
            &[
                "Memory Index Expected Instead Of",
                &ind,
                "Should Be Replaced With A Memory Index",
            ],
        )
    }
    pub fn check_all_if_mem_ind(real_line: &[String], line: &[String]) -> String {
        let mut out: String = String::new();
        line[1..].iter().cloned().for_each(|x| {
            out = Self::check_if_mem_ind(real_line, x);
        });
        out
    }

    pub fn check_if_dangerous_mem_ind(real_line: &[String], ind: String) -> String {
        if let Ok(x) = ind.parse::<u64>() {
            if x < 38 {
                return Universal::format_error(
                    real_line,
                    &["Memory Index", &ind, "Endangers A Read-Only Memory Index"],
                );
            }
            return String::new();
        }
        Universal::format_error(
            real_line,
            &[
                "Memory Index Expected Instead Of",
                &ind,
                "Should Be Replaced With A Memory Index",
            ],
        )
    }

    pub fn errors_to_string(vec: Vec<String>) -> String {
        let mut out: String = String::new();
        let mut vec2 = vec;
        vec2.dedup();
        vec2.iter()
            .cloned()
            .for_each(|x| out.push_str(&("\n".to_string() + &x)));
        return out.trim().to_string();
    }
}

pub trait GenericCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self>
    where
        Self: Sized;
    fn analyze(&self) -> String;
    fn compile(&self) -> Vec<u8>;
}

pub struct UnrecognizedCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for UnrecognizedCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        Box::new(UnrecognizedCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        })
    }
    fn analyze(&self) -> String {
        Universal::format_error(
            &self.real_line,
            &["Command", &self.line[0], "Does Not Exist"],
        )
    }
    fn compile(&self) -> Vec<u8> {
        vec![255]
    }
}

pub struct WvarCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for WvarCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: WvarCommand = WvarCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![0, (self.line.len() - 1).try_into().unwrap()];
        (1..self.line.len())
            .into_iter()
            .for_each(|x| out.push(Universal::quick_parse_u8(self.line[x].clone())));
        out
    }
}

pub struct NvarCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for NvarCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: NvarCommand = NvarCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 1);
        if !length_err.is_empty() {
            return length_err;
        }
        Command::errors_to_string(vec![
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_if_mem_ind(&self.real_line, self.line[1].clone()),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        vec![1, Universal::quick_parse_u8(self.line[1].clone())]
    }
}

pub struct TrimCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for TrimCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: TrimCommand = TrimCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 2);
        if !length_err.is_empty() {
            return length_err;
        }
        let mut errors: Vec<String> = vec![
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_if_mem_ind(&self.real_line, self.line[1].clone()),
        ];
        let res: Result<u64, _> = self.line[2].parse::<u64>();
        if res.is_err() {
            errors.push(Universal::format_error(
                &self.line,
                &[
                    "Trim Length Expected Instead Of",
                    &self.line[2],
                    "Should Be Replaced With A Trim Length",
                ],
            ));
        }
        if res.unwrap() > 255 {
            errors.push(Universal::format_error(
                &self.line,
                &[
                    "Trim Length",
                    &self.line[2],
                    "Is Larger Than 255 And Will Not Be Compiled Properly",
                ],
            ));
        }
        Command::errors_to_string(errors)
    }
    fn compile(&self) -> Vec<u8> {
        vec![
            2,
            Universal::quick_parse_u8(self.line[1].clone()),
            Universal::quick_parse_u8(self.line[2].clone()),
        ]
    }
}

pub struct MathCommand {
    real_line: Vec<String>,
    line: Vec<String>,
    ind: u64,
}

impl GenericCommand for MathCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: MathCommand = MathCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
            ind: match line[0].to_lowercase().as_str() {
                "add" => 3,
                "sub" => 4,
                "mul" => 5,
                "div" => 6,
                "mod" => 7,
                "rmod" => 8,
                _ => 255,
            },
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 2);
        if !length_err.is_empty() {
            return length_err;
        }
        Command::errors_to_string(vec![
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        vec![
            self.ind as u8,
            Universal::quick_parse_u8(self.line[1].clone()),
            Universal::quick_parse_u8(self.line[2].clone()),
        ]
    }
}

pub struct NopCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for NopCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: NopCommand = NopCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        Command::check_arg_length(&self.real_line, &self.line, 0)
    }
    fn compile(&self) -> Vec<u8> {
        vec![9]
    }
}

pub struct JumpCommand {
    real_line: Vec<String>,
    line: Vec<String>,
    ind: u64,
}

impl GenericCommand for JumpCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: JumpCommand = JumpCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
            ind: match line[0].to_lowercase().as_str() {
                "jm" => 10,
                "jl" => 11,
                "je" => 12,
                "jne" => 13,
                _ => 255,
            },
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 3);
        if !length_err.is_empty() {
            return length_err;
        }
        let mut errors: Vec<String> = vec![
            Command::check_if_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_if_mem_ind(&self.real_line, self.line[2].clone()),
        ];
        let res: Result<u64, _> = self.line[3].parse::<u64>();
        if res.is_err() {
            errors.push(Universal::format_error(
                &self.line,
                &[
                    "Command Number Expected Instead Of",
                    &self.line[3],
                    "Should Be Replaced With A Command Number",
                ],
            ));
        }
        if res.unwrap() > 65535 {
            errors.push(Universal::format_error(
                &self.line,
                &[
                    "Command Number",
                    &self.line[3],
                    "Is Larger Than 65535 And Will Not Be Compiled Properly",
                ],
            ));
        }
        Command::errors_to_string(errors)
    }
    fn compile(&self) -> Vec<u8> {
        let command_num: i32 = self.line[3].parse::<i32>().unwrap();
        vec![
            self.ind as u8,
            Universal::quick_parse_u8(self.line[1].clone()),
            Universal::quick_parse_u8(self.line[2].clone()),
            (command_num >> 8) as u8,
            (command_num << 8 >> 8) as u8,
        ]
    }
}

pub struct PrintCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for PrintCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: PrintCommand = PrintCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 254),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![14, (self.line.len() - 1).try_into().unwrap()];
        self.line[1..]
            .iter()
            .cloned()
            .for_each(|x| out.push(Universal::quick_parse_u8(x)));
        out
    }
}

pub struct ReadCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for ReadCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: ReadCommand = ReadCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 1);
        if !length_err.is_empty() {
            return length_err;
        }
        Command::errors_to_string(vec![
            Command::check_if_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        vec![15, Universal::quick_parse_u8(self.line[1].clone())]
    }
}

pub struct WfileCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for WfileCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: WfileCommand = WfileCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![16, (self.line.len() - 1).try_into().unwrap()];
        (1..self.line.len())
            .into_iter()
            .for_each(|x| out.push(Universal::quick_parse_u8(self.line[x].clone())));
        out
    }
}

pub struct RfileCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for RfileCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: RfileCommand = RfileCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![17, (self.line.len() - 1).try_into().unwrap()];
        (1..self.line.len())
            .into_iter()
            .for_each(|x| out.push(Universal::quick_parse_u8(self.line[x].clone())));
        out
    }
}

pub struct DfileCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for DfileCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: DfileCommand = DfileCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![18, (self.line.len() - 1).try_into().unwrap()];
        (1..self.line.len())
            .into_iter()
            .for_each(|x| out.push(Universal::quick_parse_u8(self.line[x].clone())));
        out
    }
}

pub struct WfuncCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for WfuncCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: WfuncCommand = WfuncCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let func_name: &[String] = &Universal::convert_to_mem(
            &self.line[1],
            false,
            &HashMap::<String, u8>::new(),
            &MemoryMap::new_limited(),
        );
        let func_args: &[String] = &self.line[2..];
        Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, func_name, 65535),
            Command::check_arg_length_using_limit(&self.real_line, func_args, 255),
            Command::check_all_if_mem_ind(&self.real_line, func_args),
        ])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![19];
        let func_name: &[String] = &Universal::convert_to_mem(
            &self.line[1],
            false,
            &HashMap::<String, u8>::new(),
            &MemoryMap::new_limited(),
        );
        let func_name_len: u16 = func_name.len().try_into().unwrap();
        out.push((func_name_len >> 8) as u8);
        out.push((func_name_len << 8 >> 8) as u8);
        func_name
            .iter()
            .cloned()
            .for_each(|x| out.push(Universal::quick_parse_u8(x)));
        let func_args: &[String] = &self.line[2..];
        out.push(func_args.len().try_into().unwrap());
        func_args
            .iter()
            .cloned()
            .for_each(|x| out.push(Universal::quick_parse_u8(x)));
        out
    }
}

pub struct CfuncCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for CfuncCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: CfuncCommand = CfuncCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let func_name: &[String] = &Universal::convert_to_mem(
            &self.line[1],
            false,
            &HashMap::<String, u8>::new(),
            &MemoryMap::new_limited(),
        );
        Command::errors_to_string(vec![Command::check_arg_length_using_limit(
            &self.real_line,
            func_name,
            65535,
        )])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![20];
        let func_name: &[String] = &Universal::convert_to_mem(
            &self.line[1],
            false,
            &HashMap::<String, u8>::new(),
            &MemoryMap::new_limited(),
        );
        let func_name_len: u16 = func_name.len().try_into().unwrap();
        out.push((func_name_len >> 8) as u8);
        out.push((func_name_len << 8 >> 8) as u8);
        func_name
            .iter()
            .cloned()
            .for_each(|x| out.push(Universal::quick_parse_u8(x)));
        out
    }
}

pub struct UfuncCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for UfuncCommand {
    fn create(real_line: &[String], line: &[String]) -> Box<Self> {
        let out: UfuncCommand = UfuncCommand {
            real_line: real_line.to_vec(),
            line: line.to_vec(),
        };
        Box::new(out)
    }
    fn analyze(&self) -> String {
        let func_name: &[String] = &Universal::convert_to_mem(
            &self.line[1],
            false,
            &HashMap::<String, u8>::new(),
            &MemoryMap::new_limited(),
        );
        Command::errors_to_string(vec![Command::check_arg_length_using_limit(
            &self.real_line,
            func_name,
            65535,
        )])
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![21];
        let func_name: &[String] = &Universal::convert_to_mem(
            &self.line[1],
            false,
            &HashMap::<String, u8>::new(),
            &MemoryMap::new_limited(),
        );
        let func_name_len: u16 = func_name.len().try_into().unwrap();
        out.push((func_name_len >> 8) as u8);
        out.push((func_name_len << 8 >> 8) as u8);
        func_name
            .iter()
            .cloned()
            .for_each(|x| out.push(Universal::quick_parse_u8(x)));
        let func_args: &[String] = &self.line[2..];
        out.push(func_args.len().try_into().unwrap());
        func_args
            .iter()
            .cloned()
            .for_each(|x| out.push(Universal::quick_parse_u8(x)));
        out
    }
}
