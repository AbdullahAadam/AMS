/**
 * 
 */
const form=document.querySelector("form");
const tog = document.getElementById("tog");
const pwd = document.getElementById("pwd");
const email = document.getElementById("email");
const eemail = document.getElementById("eemail");
const epwd = document.getElementById("epwd");
const dis = document.getElementsByClassName("error");
tog.addEventListener("click", function() {
	if (tog.checked) {
		pwd.setAttribute('type', 'text')
	} else {
		pwd.setAttribute('type', 'password')
	}
});

console.dir(form);

email.addEventListener("keydown", function() {

	//alert("onkeydown")
	eemail.style.display = 'none';
	email.style.borderColor = 'rgb(76, 128, 238)';
});
pwd.addEventListener("keydown", () => {
	epwd.style.display = 'none';
	pwd.style.borderColor = 'rgb(76, 128, 238)';
});
email.addEventListener("change", () => {
	email.style.border = '1px solid #fff';
	email.style.backgroundColor = 'none';
});
pwd.addEventListener("change", () => {
	pwd.style.border = '1px solid #fff';
	pwd.style.backgroundColor = 'none';

});
function validation() {


	console.log(form.elements[1].value)
	const emailregex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	let emailVal = emailregex.test(email.value);
	// function change(ele){
	// ele.style.display="grid";
	// ele.style.justifyContent="left";
	// }
	//alert("Hello");
	console.log(pwd.value, email.value);
	if (!(email.value == '')) {
		//dis.style.display='block'
		//change(eemail);

		//  dis.style.display='block';
		//  dis.style.textAlign='left';
		//  eemail.style.display='block';

		// eemail.style.justifyContent="left";
		if (!emailVal) {
			eemail.innerHTML = "*Invalid Email";
			eemail.style.display = 'block';
			eemail.style.textAlign = 'left';
			email.style.borderColor = 'red';
			email.focus();
			return false;
		}
	} else {
		//eemail.innerHTML="*Email must required";
		eemail.style.display = 'block';
		eemail.style.textAlign = 'left';
		email.style.borderColor = 'red';
		email.focus();
		return false;
	}
	if (pwd.value == "") {
		epwd.style.display = 'block';
		epwd.style.textAlign = 'left'
		pwd.style.borderColor = 'red';
		pwd.focus();
		return false;
		//errors(pwd);
	}

}