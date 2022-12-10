use std::fs;

use crate::universal;

use regex::Captures;
use regex::Regex;

pub struct UFBC{
    pub file_name: String
}

impl UFBC{
    pub fn compile(&self){
        let mut code: String=match fs::read_to_string(&self.file_name){
            Ok(content) => content,
            Err(_e) => {
                universal::err_exit(
                    concat!(
                        "File Provided Does Not Exist...\n",
                        "Terminating..."
                    ).to_string()
                );
                return ();
            }
        };
        let line: Regex=Regex::new("(\n)+").unwrap();
        let string: Regex=Regex::new("(.*)(\".*\")(.*)").unwrap();
        {
            let comments: Regex=Regex::new("//[^\n]+").unwrap();
            let multi_liners: Regex=Regex::new(
                "/\\*(?:.|\n)*?+\\*/"
            ).unwrap();
            let empty_lines: Regex=Regex::new("\n\n+").unwrap();
            code=empty_lines.replace_all(
                &comments.replace_all(
                    &multi_liners.replace_all(
                        &code, "".to_string()
                    ).to_string(), "".to_string()
                ).to_string(), "".to_string()
            ).to_string();
        }
        let mut lines: Vec<String>;
        {
            let orig_lines: Vec<&str>=line.split(&code)
                                     .into_iter()
                                     .collect::<Vec<&str>>();
            lines=Self::convert_dividers_in_string(&orig_lines, string);
        }
        println!();
        println!("{}", universal::arr_to_string2(&lines, '\n'));
    }
    fn convert_dividers_in_string<'a>(
        lines: &'a Vec<&'a str>, string: Regex
    ) -> Vec<String>{
        let mut out: Vec<String>=Vec::<String>::new();
        let mut i=0;
        while i != lines.len(){
            let result: Option<Captures>=string.captures(lines[i]);
            match result{
                None => {
                    out.push(lines[i].to_string());
                },
                Some(cap) => {
                    let groups: [&str; 3]=[
                        cap.get(1).unwrap().as_str(),
                        cap.get(2).unwrap().as_str(),
                        cap.get(3).unwrap().as_str(),
                    ];
                    let mut res: String=String::new();
                    let dividers: Regex=Regex::new("[-|, \t]").unwrap();
                    for x in groups[1].chars(){
                        if dividers.is_match(&String::from(x)){
                            res=format!("{}UU{}", res, (match x {
                                '-'  => "0045",
                                '|'  => "0124",
                                ','  => "0044",
                                ' '  => "0032",
                                '\t' => "0009",
                                _    => "0000", // w. t. h. compiler.
                            }));
                        }else{
                            res=format!("{}{}", res, x);
                        }
                    }
                    out.push(res);
                }
            }
            i+=1;
        }
        return out;
    }
}
