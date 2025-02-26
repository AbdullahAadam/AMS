/**
 * 
 */
$(document).ready(function(){
		/*$(".menu-item a").filter(function() {
		       return $(this).text().trim() === "Add Professor";  // Matching exact text
		   }).on("click", function() {
		       toastr.warning("The Professor ID is created automatically.");
		   });*/
		   $.ajax({
		   	    url:'/admin/listDepartments',
		   	    type: 'GET',
		   	    dataType: 'json',
		   	    success: function(data) {
		   			console.log(data);
		   		    $.each(data, function(index, department) {
		   		       $('#department').append(
		   		       	$('<option>', {
		   					value: department.deptId,
		   					text: department.deptName,
							id:department.code
		   		         })
		   		       );
		   		    });
		   	    },
		   	    error: function(xhr, status, error) {
		   	        console.log("Error fetching departments:", error);
		   	    }
		   	 });
			 $("#department").change(function(){
				var deptId=$(this).val();
				$("#professor").empty().append('<option value="">Select Professor</option>');
				if(deptId){
					$.ajax({
						url:"/admin/professors/"+deptId,
						type:"GET",
						success:function(data){
							console.log(data);
							$.each(data,function(index,professor){
								$("#professor").append('<option value="'+professor.profId+'">'+professor.name+'</option>');
							});
						}
					});
				}
			 });
           function validateForm(){
               let isValid=true;
               let InvalidField=null;
               $(".error-message").hide();
               $("input").removeClass("error-border");
               const studentRegNo=$("#studentRegNo").val();
               const regNoPattern = /^(\d{4})([A-Za-z]{3,5})(\d{3})$/;
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
                       $("#studentRegNoError").text("RegNo year must be between 2010 and "+Year).show();
                       $("#studentRegNo").addClass("error-border");
                       if(!InvalidField)InvalidField=$("#studentRegNo");
                       isValid=false;
                   }else{
					const extractedDept=studentRegNo.match(regNoPattern)[2];
					let matched=false;
					$("#department option").each(function(){
					   if($(this).attr('id') === extractedDept){ // Check department code
					        $(this).prop("selected", true);
					        matched = true;
					        return false; // Break loop
					    }
					});
					if(!matched){
					        $("#studentRegNoError").text("No matching department found for this RegNo.").show();
					        $("#studentRegNo").addClass("error-border");
					        isValid = false;
					}
				   }
               }
               const studentName=$("#studentName").val();
               if(!studentName){
                   $("#studentNameError").text("Student Name is required.").show();
                   $("#studentName").addClass("error-border");
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
			   const professor=$("#professor").val();
			   if(!professor){
			         	$("#professorError").text("Professor must required").show();
			            $("#professor").addClass("error-border");
			            if(!InvalidField)InvalidField=$("#professor");
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
		   $("#studentRegNo").on('input', function(){
		          const studentRegNo = $(this).val().trim();
		          const regNoPattern = /^(\d{4})([A-Za-z]{3,5})(\d{3})$/;
		          
		          if(regNoPattern.test(studentRegNo)){
		              const extractedDept = studentRegNo.match(regNoPattern)[2]; // Extract department code
		              let matched = false;

		              $("#department option").each(function(){
		                  if($(this).attr('id') === extractedDept){ // Match department code with option ID
		                      $(this).prop("selected", true);
		                      matched = true;
		                      return false; // Break the loop
		                  }
		              });

		              if(!matched){
		                  $("#studentRegNoError").text("No matching department found.").show();
		                  $("#studentRegNo").addClass("error-border");
		              } else {
		                  $("#studentRegNoError").hide();
		                  $("#studentRegNo").removeClass("error-border");
		              }
		          }
		      });
           $("#studentRegNo,#studentName,#email").keydown(function(){
               $(".error-message").hide();
               $("input,select").removeClass("error-border");
           });
		   $("#department,#professor").click(function(){
		   		$(".error-message").hide();
		   		$("select").removeClass("error-border");
		   });
           $("#addStudentForm").submit(function(event){
               event.preventDefault();
               if(!validateForm()){
                   return;
               }
           });
          
       });