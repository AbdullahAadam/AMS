/**
 * 
 */
const form=document.querySelector("form");
        const pwd=document.getElementById("pwd");
        const cpwd=document.getElementById("cpwd");
        const epwd=document.getElementById("epwd");
        const ecpwd=document.getElementById("ecpwd");
        const er=document.getElementById("e");
        //let pwdv,cpwdv;
        let sep='!@#$%^&*()-+.';
        const regex =new RegExp("["+sep+"]");
        console.dir(form); 
        pwd.addEventListener("keydown",function(){
            //alert("onkeydown")
            epwd.style.display='none';
            pwd.style.borderColor='rgb(76, 128, 238)';
        });
        cpwd.addEventListener("keydown",()=>{
            ecpwd.style.display='none';
            er.style.display='none';
            cpwd.style.borderColor='rgb(76, 128, 238)';
        });
        //pwd.addEventListener("change",pwdchecker);
        cpwd.addEventListener("change",cpwdchecker);
        function pwdchecker(){
            if(pwd.value=='' ){
                epwd.style.display='block';
                pwd.style.borderColor='red';
                pwd.focus();
                return false;    
            }
            return pwdmanager();       
        }
        function pwdmanager(){
            const pwdv=pwd.value;
            console.log(pwdv);
            if(pwd.value.length>=10){
                if(/[A-Z]/.test(pwdv)){
                    if(/[0-9]/.test(pwdv) ){
                        if(regex.test(pwdv)){
                            //alert("hello");
                            pwd.style.borderColor="darkgreen";
                            epwd.innerHTML='*Enter the Password';
                            epwd.style.display='none';
                            //cpwdchecker();
                            return true;
                        }else{
                            epwd.innerHTML='Password atleast have  Special Characters';
                            epwd.style.display='block';
                            pwd.focus();
                            return false;
                        }
                    }else{
                        epwd.innerHTML='Password atleast have one numbers ';
                        epwd.style.display='block';
                        pwd.focus();
                        return false;
                    }
                }else{
                    epwd.innerHTML='Password atleast have one UpperCase';
                    epwd.style.display='block';
                    pwd.focus();
                    return false;
                }
            }else{
                epwd.innerHTML='Password atleast contains 10 character';
                epwd.style.display='block';
                pwd.focus();
                return false;
            }    
        }
        function cpwdchecker(){
            //cpwdv=cpwd.value();
            console.log(cpwd.value)
            if(cpwd.value==''){
                ecpwd.style.display='block';
                cpwd.style.borderColor='red';
                    //pwd.style.setProperty('--placeholder-color','red');
                 //pwd.style.color='red';
                cpwd.focus();
                    //return false;    
            }else if(cpwd.value!==pwd.value){
                //cpwdv=cpwd.value();
                    cpwd.focus();
                    er.style.display='block';
                    return false;
            }else{
                er.style.display='none';
                return true;
            }
        }
        function validation(event){
            if(pwdchecker() && cpwdchecker()){
                return true;
            }else{
                event.preventDefault();
                return false;
            } 
        }