pub struct FlagManager {
    pub compile_flag: bool,
    pub version_flag: bool,
    pub license_flag: bool,
    pub perfmes_flag: bool,
    pub nanosec_flag: bool,
    pub commmes_flag: bool,
    pub file_name: String,
}

impl FlagManager {
    pub fn new(args: &Vec<String>) -> FlagManager {
        let nanosec_flag: bool = Self::check_flag(args, "n".to_string());
        let commmes_flag: bool = Self::check_flag(args, "m".to_string());
        FlagManager {
            compile_flag: Self::check_flag(args, "c".to_string()),
            version_flag: Self::check_flag(args, "v".to_string()),
            license_flag: Self::check_flag(args, "l".to_string()),
            perfmes_flag: nanosec_flag || commmes_flag || Self::check_flag(args, "p".to_string()),
            nanosec_flag,
            commmes_flag,
            file_name: Self::get_file_name(args),
        }
    }
    fn check_flag(args: &Vec<String>, flag: String) -> bool {
        for x in args {
            if x.starts_with('-') && x.contains(&flag) {
                return true;
            }
        }
        false
    }
    fn get_file_name(args: &Vec<String>) -> String {
        for x in args {
            if x.ends_with(".ufb") || x.ends_with(".ufbb") {
                return x.to_string();
            }
        }
        String::new()
    }
}
