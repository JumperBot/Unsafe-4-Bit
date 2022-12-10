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
