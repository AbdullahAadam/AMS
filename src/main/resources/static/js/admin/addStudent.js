/**
 * 
 */
$(document).ready(function(){
		$(".menu-item a").filter(function() {
		       return $(this).text().trim() === "Add Professor";  // Matching exact text
		   }).on("click", function() {
		       toastr.warning("The Professor ID is created automatically.");
		   });
           function validateForm(){
               let isValid=true;
               let InvalidField=null;
               $(".error-message").hide();
               $("input").removeClass("error-border");
               const studentRegNo=$("#studentRegNo").val();
               const regNoPattern = /^(\d{4})([A-Za-z]{2,4})(\d{3})$/;
               const Year = new Date().getFullYear()+1; 
               if(!studentRegNo){
                   $("#studentRegNoError").text("RegNo must required").show();
                   $("#studentRegNo").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#studentRegNo");
                   isValid=false;
                   
               }else if(!regNoPattern.test(studentRegNo)){
                   $("#studentRegNoError").text("Invalid RegNo ").show();
                   $("#studentRegNo").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#studentRegNo");
                   isValid=false;
               }else{
                   const regnoYear=parseInt(studentRegNo.match(regNoPattern)[1],10);
                   if(regnoYear<2010 || regnoYear>Year){
                       $("#stydentRegNoError").text("RegNo year must be between 2010 and "+Year).show();
                       $("#studentRegNo").addClass("error-border");
                       if(!InvalidField)InvalidField=$("#studentRegNo");
                       isValid=false;
                   }
               }
               const studentName=$("#studentName").val();
               if(!studentName){
                   $("#studentNameError").text("Student Name is required.").show();
                   $("#StudentName").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#studentName");
                   isValid=false;
               }else if(studentName.length <3 || studentName.length>50){
                   $("#studentNameError").text("Student Name must between 3 to 50 characters").show();
                   $("#studentName").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#studentName");
                   isValid=false;
               }
               const department=$("#department").val();
               if(!department){
                   $("#departmentError").text("Department must required").show();
                   $("#department").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#department");
                   isValid=false;
               }
               const email=$("#email").val().trim();
               const emailPattern=/^[^\s@]+@[^\s@]+\.[^\s@]+$/;
               if(!email){
                   $("#emailError").text("Email must required").show();
                   $("#email").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#email");
                   isValid=false;
               }else if(!emailPattern.test(email)){
                   $("#EmailError").text("Invalid Email Format").show();
                   $("#email").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#email");
                   isValid=false;
               }
               if(InvalidField){
                   InvalidField.focus();
               }
               return isValid;
           }
           $("#studentRegNo,#studentName,#department,#email").keydown(function(){
               $(".error-message").hide();
               $("input,select").removeClass("error-border");
           });
           $("#addStudentForm").submit(function(event){
               event.preventDefault();
               if(!validateForm()){
                   return;
               }
           });
          
       });