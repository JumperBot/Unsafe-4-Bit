pub fn convert_u32_to_char(code: u32) -> char{
    let c=match char::from_u32(code){
        None => '\u{0000}',
        Some(c) => c,
    };
    return c;
}
pub fn convert_borrowed_u32_to_char(code: &u32) -> char{
    let c=match char::from_u32(*code){
        None => '\u{0000}',
        Some(c) => c,
    };
    return c;
}

pub fn arr_to_string<T: std::fmt::Debug>(arr: &[T]) -> String{
    let mut out: String=String::new();
    let mut i: usize=0;
    while i != arr.len(){
        out=format!("{out}{:?}", arr[i]);
        i+=1;
        if i != arr.len(){
            out+=", ";
        }
    }
    return out;
}

pub fn arr_to_string2<T: std::fmt::Debug>(arr: &[T], c: char) -> String{
    let mut out: String=String::new();
    let mut i: usize=0;
    while i != arr.len(){
        out=format!("{out}{:?}", arr[i]);
        i+=1;
        if i != arr.len(){
            out+=&String::from(c);
        }
    }
    return out;
}

pub fn err_exit(err_msg: String){
    println!(
        "\u{001B}[91m{}\u{001B}[0m",
        err_msg
    );
    std::process::exit(1);
}

pub fn manage_padding(input: String, padding: usize) -> String{
    let mut out=String::new();
    while out.len()+input.len() != padding{
        out=out+"0";
    }
    return format!("{}{}", out, input);
}
