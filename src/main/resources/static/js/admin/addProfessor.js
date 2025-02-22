/**
 * 
 */
$(document).ready(function(){
	//alter('professor');
	//console.log("professor.js");
	//console.log(showToastr);
	//console.log(toastrType);
	//console.log(toastrMessage);
	
		//var toastrType='[[${toastrType}]]';
		//var toastrMessage='[[${toastrMessage}]]';
		/*switch(toastrType){
			case "success":
				toastr.success(toastrMessage);
				break;
			case "error":
				toastr.error(toastrMessage);
				break;
			case "info":
				toastr.info(toastrMessage);
				break;
			case "warning":
				toastr.warning(toastrMessage);
				break;
			default:
				console.log("invalid Toastr");
		}*/
		$(".menu-item a").filter(function() {
		       return $(this).text().trim() === "Add Professor";  // Matching exact text
		   }).on("click", function() {
		       toastr.warning("The Professor ID is created automatically.");
		   });
            function validateForm(){
                let isValid=true;
                let InvalidField=null;
                $(".error-message").hide();
                $("input,select").removeClass("error-border");
                const professorName=$("#professorName").val().trim();
                if(!professorName){
                    $("#professorNameError").text("Professor Name is required.").show();
                    $("#professorName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#professorName");
                    isValid=false;
                }else if(professorName.length <3 || professorName.length>50){
                    $("#professorNameError").text("Professor Name must between 3 to 50 characters").show();
                    $("#professorName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#professorName");
                    isValid=false;
                }
                const email=$("#email").val().trim();
                const emailPattern=/^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if(!email){
                    $("#professorEmailError").text("Email must required").show();
                    $("#email").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#email");
                    isValid=false;
                }else if(!emailPattern.test(email)){
                    $("#professorEmailError").text("Invalid Email Format").show();
                    $("#email").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#email");
                    isValid=false;
                }
                const professorRole=$("#professorRole").val();
                if(!professorRole){
                    $("#professorRoleError").text("Role must required").show();
                    $("#professorRole").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#professorRole");
                    isValid=false;
                }
                if(InvalidField){
                    InvalidField.focus();
                }
                $("#professorName,#email").keydown(function(){
                    $(".error-message").hide();
                    $("input,select").removeClass("error-border");
                });
                return isValid;
            }
            function creatingId(){
                var name=$("#professorName").val().trim();
                var role=$("#professorRole").val();
                var totalLetters=name.replace(/\s+/g,'').length;
                const num=Math.floor(Math.random()*100);
                var nameParts=name.split(" ");
                // if(!name){
                //     $("#professorName").val('');
                //     return;
                // }
                var initial='';
                for(var i=0;i<nameParts.length;i++){
                    initial=initial+nameParts[i].charAt(0).toUpperCase();
                }
                var professorId=role+initial+totalLetters+"-"+num;
                $("#professorId").val(professorId);
            }
            $("#professorName,#professorRole").on('input change',function(){
                creatingId();
            });
            $("#addProfessorForm").submit(function(event){
                event.preventDefault();
                if(!validateForm()){
                    return;
                }
            });
        });
        