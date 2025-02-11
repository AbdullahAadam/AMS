/**
 * 
 */
const form=document.querySelector("form");
const name=document.getElementById("name");
const email=document.getElementById("email");
const ename=document.getElementById("ename");
const eemail=document.getElementById("eemail");
        console.dir(form);
        email.addEventListener("keydown",function(){
            //alert("onkeydown")
            eemail.style.display='none';
            email.style.borderColor='rgb(76, 128, 238)';
        });
        name.addEventListener("keydown",()=>{
            ename.style.display='none';
            name.style.borderColor='rgb(76, 128, 238)';
        });
        email.addEventListener("change",()=>{
            email.style.border='1px solid #fff';
            email.style.backgroundColor='none';
        });
        name.addEventListener("change",()=>{
            name.style.border='1px solid #fff';
            name.style.backgroundColor='none';
        });
        function validation(){
            const emailregex=/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            let emailVal=emailregex.test(email.value);
            console.log(email.value)
            if(!(email.value=='')){
                if(!emailVal){
                    eemail.innerHTML="*Invalid Email ";
                    eemail.style.display='block';
                    email.style.borderColor='red';
                    email.focus();
                    return false;
                }
            }else{
                //eemail.innerHTML="*Email must required";
                eemail.style.display='block';
                email.style.borderColor='red';
                email.focus();
                return false;
            }
			if(name.value==""){
			               ename.style.display='block';
			               ename.style.textAlign='left'
			               name.style.borderColor='red';
			               name.focus();
			               return false;
			           }
          
        }