/**
 * 
 */
$(document).ready(function(){
				$.get("/csrf", function(data) {
					    // data might look like: { parameterName: "_csrf", token: "abc123", headerName: "X-CSRF-TOKEN" }
					    window.csrfToken = data.token;
					    window.csrfHeader = data.headerName;
					    console.log("CSRF Token retrieved:", window.csrfToken);
				});
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
                const departmentName=$("#departmentName").val().trim();
                if(!departmentName){
                    $("#departmentNameError").text("Department Name must required").show();
                    $("#departmentName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentName");
                    isValid=false;
                }else if(departmentName.length < 4 || departmentName.length>50){
                    $("#departmentNameError").text("Department name must between 5 to 50 characters").show();
                    $("#departmentName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentName");
                    isValid=false;
                }
                /*const departmentHod=$("#departmentHod").val().trim();
                if(!departmentHod){
                    $("#departmentHodError").text("HOD name must required").show();
                    $("#departmentHod").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentHod");
                    isValid=false;
                }else if(departmentHod.length <3 || departmentHod.length>50){
                    $("#departmentHodError").text("HOD name must between 3 to 50 characters").show();
                    $("#departmentHod").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentHod");
                    isValid=false;
                }
                if((departmentName && departmentHod && departmentName.toUpperCase()===departmentHod.toUpperCase())){
                    $("#departmentNameError,#departmentHodError").text("Invalid Name").show();
                    $("#departmentName,#departmentHod").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentName,#departmentHod");
                    isValid=false;
                }*/
                const departmentPeriod=parseInt($("#departmentPeriod").val());
                if(!departmentPeriod){
                    $("#departmentPeriodError").text("Period must required").show();
                    $("#departmentPeriod").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentPeriod");
                    isValid=false;
                }else if(departmentPeriod<3 || departmentPeriod>10){
                    $("#departmentPeriodError").text("Period must atleast contains 3 to 10").show();
                    $("#departmentPeriod").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentPeriod");
                    isValid=false;
                }
                const departmentYear=parseInt($("#departmentYear").val());
                if(!departmentYear){
                    $("#departmentYearError").text("Year must required").show();
                    $("#departmentYear").addClass(".error-border");
                    if(!InvalidField)InvalidField=$("#departmentYear");
                    isValid=false;
                }
                if(InvalidField){
                    InvalidField.focus();
                }
                return isValid;
            }
            $("#departmentName,#departmentPeriod").keydown(function(){
                $(".error-message").hide();
                $("input,select").removeClass("error-border");
            });
            $("#addDepartmentForm").submit(function(event){
                event.preventDefault();
                if(!validateForm()){
                    return;
                }
				departmentData={
					deptId:$("#departmentId").val(),
					deptName:$("#departmentName").val(),
					code:$("#departmentCode").val(),
					year:parseInt($("#departmentYear").val()),
					period:$("#departmentPeriod").val(),
					sem:parseInt($("#departmentYear").val())*2,
				};
				console.log(departmentData);
				//var csrfToken = $("meta[name='_csrf']").attr("content");
				//var csrfHeader = $("meta[name='_csrf_header']").attr("content");
				//console.log(csrfToken);
				//console.log(csrfHeader);
				$.ajax({
					url:"/admin/addDepartment",
					method:"POST",
					contentType:"application/json",
					xhrFields:{
						withCredentials: true
					},
					headers: { [window.csrfHeader]: window.csrfToken },
					data:JSON.stringify(departmentData),
					success:function(response){
						console.log("Success: ",response);
						toastr.success(response);
						$("#addDepartmentForm").trigger("reset");
					},
					error:function(xhr,status,error){
						if(xhr.responseText.indexOf("already exists")!==-1){
							toastr.warning("Department already exists");
						}else{
							toastr.error(xhr.responseText||"An unexpected error occur");
						}
					}
				});	
            });
            function creatingIdCode(){
                const departmentName=$("#departmentName").val();
                const words=departmentName.split(' ');
                const num=Math.floor(Math.random()*100);
                let initial='';
                if(words.length===1){
					if(words[0].length<=5){
						initial=words[0].toUpperCase();
					}else{
						initial=words[0].slice(0,3).toUpperCase();
						console.log(words[0].slice(0,3))
					} 
                }else{
                    words.forEach(element => {
                        if(element.toLowerCase() !=='of' && element.toLowerCase()!=='in'){
                            initial=initial+element.charAt(0).toUpperCase();
                       
                        }
                    });
                }
                $("#departmentCode").val(initial);
                $("#departmentId").val(initial+num);
            }
            $("#departmentName").on('input change',function(){
                creatingIdCode();
            })
        });