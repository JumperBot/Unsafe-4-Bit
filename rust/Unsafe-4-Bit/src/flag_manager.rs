pub struct FlagManager{
    pub compile_flag: bool,
    pub file_name: String,
}
impl FlagManager{
    pub fn new(args: &Vec<String>) -> FlagManager{
        return FlagManager{
            compile_flag: Self::check_flag(args, "c".to_string()),
            file_name: Self::get_file_name(args),
        };
    }
    fn check_flag(args: &Vec<String>, flag: String) -> bool{
        for x in args{
            if x.starts_with("-")&&x.contains(&flag){
                return true;
            }
        }
        return false;
    }
    fn get_file_name(args: &Vec<String>) -> String{
        for x in args{
            if x.ends_with(".ufb")||x.ends_with(".ufbb"){
                return x.to_string();
            }
        }
        return "".to_string();
    }
}
