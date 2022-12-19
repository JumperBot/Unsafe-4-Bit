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
use crate::memory_map::MemoryMap;
use crate::universal::Universal;

use itertools::Itertools;

pub struct Command {
    pub compiled: Vec<u8>,
    pub errors: String,
    pub cancel_optimization: bool,
}

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
     * 20   -   00010100    -   dfunc
     **/
    pub fn new(line: &Vec<String>, real_line: &Vec<String>, binary_map: &MemoryMap) -> Command {
        if !binary_map.contains_key(&line[0].to_lowercase()) {
            let unrecognized: Box<UnrecognizedCommand> =
                UnrecognizedCommand::create(&real_line, &line);
            return Command {
                compiled: Vec::<u8>::new(),
                errors: unrecognized.analyze(),
                cancel_optimization: true,
            };
        }
        let mut cancel_optimization: bool = false;
        let ind: u64 = binary_map.get(&line[0].to_lowercase());
        let command: Box<dyn GenericCommand> = match ind {
            0 => WvarCommand::create(&real_line, &line),
            1 => NvarCommand::create(&real_line, &line),
            2 => TrimCommand::create(&real_line, &line),
            (3..=8) => MathCommand::create(&real_line, &line),
            9 => NopCommand::create(&real_line, &line),
            (10..=13) => JumpCommand::create(&real_line, &line),
            14 => PrintCommand::create(&real_line, &line),
            (15..=20) => {
                cancel_optimization = true;
                match ind {
                    15 => ReadCommand::create(&real_line, &line),
                    16 => WfileCommand::create(&real_line, &line),
                    17 => RfileCommand::create(&real_line, &line),
                    18 => DfileCommand::create(&real_line, &line),
                    //19 => WfuncCommand::create(&real_line, &line),
                    //20 => DfuncCommand::create(&real_line, &line),
                    _ => UnrecognizedCommand::create(&real_line, &line),
                }
            }
            _ => UnrecognizedCommand::create(&real_line, &line),
        };
        let err: String = command.analyze();
        if !err.is_empty() {
            return Command {
                compiled: Vec::<u8>::new(),
                errors: err,
                cancel_optimization: true,
            };
        }
        return Command {
            compiled: command.compile(),
            errors: String::new(),
            cancel_optimization: cancel_optimization,
        };
    }

    pub fn check_arg_length_using_limit(
        real_line: &Vec<String>,
        line: &Vec<String>,
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
        return String::new();
    }
    pub fn check_arg_length(real_line: &Vec<String>, line: &Vec<String>, len: usize) -> String {
        if line.len() != len + 1 {
            return Universal::format_error(
                real_line,
                &[
                    "Command",
                    &line[0],
                    &format!(
                        "Needs No Less And No More Than {} Arguments To Work",
                        match len {
                            0 => "Zero",
                            1 => "One",
                            2 => "Two",
                            _ => "Three",
                        }
                    ),
                ],
            );
        }
        return String::new();
    }
    pub fn check_if_mem_ind(real_line: &Vec<String>, ind: String) -> String {
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
        return Universal::format_error(
            &real_line,
            &[
                "Memory Index Expected Instead Of",
                &ind,
                "Should Be Replaced With A Memory Index",
            ],
        );
    }
    pub fn check_all_if_mem_ind(real_line: &Vec<String>, line: &Vec<String>) -> String {
        let mut out: String = String::new();
        for x in &line[1..] {
            out = Self::check_if_mem_ind(real_line, x.clone());
        }
        return out;
    }

    pub fn check_if_dangerous_mem_ind(real_line: &Vec<String>, ind: String) -> String {
        if let Ok(x) = ind.parse::<u64>() {
            if x < 38 {
                return Universal::format_error(
                    &real_line,
                    &["Memory Index", &ind, "Endangers A Read-Only Memory Index"],
                );
            }
            return String::new();
        }
        return Universal::format_error(
            &real_line,
            &[
                "Memory Index Expected Instead Of",
                &ind,
                "Should Be Replaced With A Memory Index",
            ],
        );
    }

    pub fn errors_to_string(vec: Vec<String>) -> String {
        let mut out: String = String::new();
        for x in vec.iter().unique() {
            out = format!("{out}\n{x}");
        }
        return out.trim().to_string();
    }
}

pub trait GenericCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self>
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
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        return Box::new(UnrecognizedCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        });
    }
    fn analyze(&self) -> String {
        return Universal::format_error(
            &self.real_line,
            &["Command", &self.line[0], "Does Not Exist"],
        );
    }
    fn compile(&self) -> Vec<u8> {
        return vec![255];
    }
}

pub struct WvarCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for WvarCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: WvarCommand = WvarCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        return Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![0, (self.line.len() - 1).try_into().unwrap()];
        for x in 1..self.line.len() {
            out.push(self.line[x].parse::<u8>().unwrap());
        }
        return out;
    }
}

pub struct NvarCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for NvarCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: NvarCommand = NvarCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 1);
        if !length_err.is_empty() {
            return length_err;
        }
        return Command::errors_to_string(vec![
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_if_mem_ind(&self.real_line, self.line[1].clone()),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        return vec![1, self.line[1].parse::<u8>().unwrap()];
    }
}

pub struct TrimCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for TrimCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: TrimCommand = TrimCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
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
        return Command::errors_to_string(errors);
    }
    fn compile(&self) -> Vec<u8> {
        return vec![
            2,
            self.line[1].parse::<u8>().unwrap(),
            self.line[2].parse::<u8>().unwrap(),
        ];
    }
}

pub struct MathCommand {
    real_line: Vec<String>,
    line: Vec<String>,
    ind: u64,
}

impl GenericCommand for MathCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: MathCommand = MathCommand {
            real_line: real_line.clone(),
            line: line.clone(),
            ind: match line[0].as_str() {
                "add" => 3,
                "sub" => 4,
                "mul" => 5,
                "div" => 6,
                "mod" => 7,
                "rmod" => 8,
                _ => 255,
            },
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 2);
        if !length_err.is_empty() {
            return length_err;
        }
        return Command::errors_to_string(vec![
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        return vec![
            self.ind as u8,
            self.line[1].parse::<u8>().unwrap(),
            self.line[2].parse::<u8>().unwrap(),
        ];
    }
}

pub struct NopCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for NopCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: NopCommand = NopCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        return Command::check_arg_length(&self.real_line, &self.line, 0);
    }
    fn compile(&self) -> Vec<u8> {
        return vec![9, 0];
    }
}

pub struct JumpCommand {
    real_line: Vec<String>,
    line: Vec<String>,
    ind: u64,
}

impl GenericCommand for JumpCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: JumpCommand = JumpCommand {
            real_line: real_line.clone(),
            line: line.clone(),
            ind: match line[0].as_str() {
                "jm" => 10,
                "jl" => 11,
                "je" => 12,
                "jne" => 13,
                _ => 255,
            },
        };
        return Box::new(out);
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
        return Command::errors_to_string(errors);
    }
    fn compile(&self) -> Vec<u8> {
        let command_num: i32 = self.line[3].parse::<i32>().unwrap();
        return vec![
            self.ind as u8,
            self.line[1].parse::<u8>().unwrap(),
            self.line[2].parse::<u8>().unwrap(),
            (command_num >> 8) as u8,
            (command_num << 8 >> 8) as u8,
        ];
    }
}

pub struct PrintCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for PrintCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: PrintCommand = PrintCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        return Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 254),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![14, (self.line.len() - 1).try_into().unwrap()];
        for x in &self.line[1..] {
            out.push(x.parse::<u8>().unwrap());
        }
        return out;
    }
}

pub struct ReadCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for ReadCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: ReadCommand = ReadCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        let length_err: String = Command::check_arg_length(&self.real_line, &self.line, 1);
        if !length_err.is_empty() {
            return length_err;
        }
        return Command::errors_to_string(vec![
            Command::check_if_mem_ind(&self.real_line, self.line[1].clone()),
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        return vec![15, self.line[1].parse::<u8>().unwrap()];
    }
}

pub struct WfileCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for WfileCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: WfileCommand = WfileCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        return Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![16, (self.line.len() - 1).try_into().unwrap()];
        for x in 1..self.line.len() {
            out.push(self.line[x].parse::<u8>().unwrap());
        }
        return out;
    }
}

pub struct RfileCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for RfileCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: RfileCommand = RfileCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        return Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
            Command::check_if_dangerous_mem_ind(&self.real_line, self.line[1].clone()),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![17, (self.line.len() - 1).try_into().unwrap()];
        for x in 1..self.line.len() {
            out.push(self.line[x].parse::<u8>().unwrap());
        }
        return out;
    }
}

pub struct DfileCommand {
    real_line: Vec<String>,
    line: Vec<String>,
}

impl GenericCommand for DfileCommand {
    fn create(real_line: &Vec<String>, line: &Vec<String>) -> Box<Self> {
        let out: DfileCommand = DfileCommand {
            real_line: real_line.clone(),
            line: line.clone(),
        };
        return Box::new(out);
    }
    fn analyze(&self) -> String {
        return Command::errors_to_string(vec![
            Command::check_arg_length_using_limit(&self.real_line, &self.line, 255),
            Command::check_all_if_mem_ind(&self.real_line, &self.line),
        ]);
    }
    fn compile(&self) -> Vec<u8> {
        let mut out: Vec<u8> = vec![16, (self.line.len() - 1).try_into().unwrap()];
        for x in 1..self.line.len() {
            out.push(self.line[x].parse::<u8>().unwrap());
        }
        return out;
    }
}
