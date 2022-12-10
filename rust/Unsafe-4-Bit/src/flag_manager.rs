pub struct FlagManager{
    pub args: Vec<String>,
}
impl FlagManager{
    pub fn is_compile_flag_on(&self) -> bool{
        return self.check_if_flag_exists("c".to_string());
    }
    fn check_if_flag_exists(&self, flag: String) -> bool{
        let args_copy=&self.args;
        for x in args_copy{
            if x.starts_with("-")&&x.contains(&flag){
                return true;
            }
        }
        return false;
    }
}
