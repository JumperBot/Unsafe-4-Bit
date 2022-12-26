pub struct FlagManager {
    pub compile_flag: bool,
    pub version_flag: bool,
    pub license_flag: bool,
    pub help_flag: bool,
    pub perfmes_flag: bool,
    pub nanosec_flag: bool,
    pub commmes_flag: bool,
    pub file_name: String,
}

impl FlagManager {
    pub fn new(args: &[String]) -> FlagManager {
        let nanosec_flag: bool = Self::check_flag(args, "n");
        let commmes_flag: bool = Self::check_flag(args, "m");
        FlagManager {
            compile_flag: Self::check_flag(args, "c"),
            version_flag: Self::check_flag(args, "v"),
            license_flag: Self::check_flag(args, "l"),
            help_flag: Self::check_flag(args, "h"),
            perfmes_flag: nanosec_flag || commmes_flag || Self::check_flag(args, "p"),
            nanosec_flag,
            commmes_flag,
            file_name: Self::get_file_name(args),
        }
    }
    fn check_flag(args: &[String], flag: &str) -> bool {
        for x in args {
            if x.starts_with('-') && x.contains(flag) {
                return true;
            }
        }
        false
    }
    fn get_file_name(args: &[String]) -> String {
        for x in args {
            if x.ends_with(".ufb") || x.ends_with(".ufbb") {
                return x.to_string();
            }
        }
        String::new()
    }
}
