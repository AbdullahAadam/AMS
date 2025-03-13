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
		$.ajax({
		    url:'/admin/listDepartments',
		    type: 'GET',
		    dataType: 'json',
		    success: function(data) {
				console.log(data);
			    $.each(data, function(index, department) {
			       $('#departmentName').append(
			       	$('<option>', {
						value: department.deptId,
						text: department.deptName,
						"data-sem":department.sem
						
			         })
					 
			       );
			    });
		    },
		    error: function(xhr, status, error) {
		        console.log("Error fetching departments:", error);
		    }
		 });
		 $("#departmentName").change(function(){
			let selectedDep=$(this).find(":selected");
			let maxSem=selectedDep.data("sem");
			let semDropDown=$("#semesterNumber");
			semDropDown.empty().append('<option value="">Select Semester</option>');
			if(maxSem){
				for(let i=1;i<=maxSem;i++){
					semDropDown.append(`<option value="${i}">Semester ${i}</option>`);
				}
			}
		 });
            function validateForm(){
                let isValid=true;
                let InvalidField=null;
                $(".error-message").hide();
                $("input,select").removeClass("error-border");
                const subjectName=$("#subjectName").val().trim();
                if(!subjectName){
                    $("#subjectNameError").text("Subject name must required").show();
                    $("#subjectName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#subjectName");
                    isValid=false;
                }else if(subjectName.length < 4 || subjectName.length>50){
                    $("#subjectNameError").text("Subject name must between 5 to 50 characters").show();
                    $("#subjectName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#subjectName");
                    isValid=false;
                }
                const departmentName=$("#departmentName").val();
                if(!departmentName){
                    $("#departmentNameError").text("Department must required").show();
                    $("#departmentName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#departmentName");
                    isValid=false;
                }
                const subjectType=$("#subjectType").val();
                if(!subjectType){
                    $("#subjectTypeError").text("Subject Type must required").show();
                    $("#subjectType").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#subjectType");
                    isValid=false;
                }
                const semesterNumber=parseInt($("#semesterNumber").val());
                if(!semesterNumber){
                    $("#semesterNumberError").text("Semester  must required").show();
                    $("#semesterNumber").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#semesterNumber");
                    isValid=false;
                }
                const subjectId=$("#subjectId").val();
                if(!subjectId){
                    $("#subjectIdError").text("Subject ID must required").show();
                    $("#subjectId").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#subjectId");
                    isValid=false;
                }
                if(InvalidField){
                    InvalidField.focus();
                }
                return isValid;
            }
            $("#subjectName,#subjectId").keydown(function(){
                $(".error-message").hide();
                $("input,select").removeClass("error-border");
            });
			$("#departmentName,#semesterNumber,#subjectType").click(function(){
				$(".error-message").hide();
				$("select").removeClass("error-border");
			});
            $("#addsubjectForm").submit(function(event){
                event.preventDefault();
                if(!validateForm()){
                    return;
                }				
				subjectData={
					subId:$("#subjectId").val(),
					name:$("#subjectName").val().trim(),
					type:$("#subjectType").val(),
					deptId:$("#departmentName").val(),
					semNo:parseInt($("#semesterNumber").val().trim()),					
				};
				console.log(subjectData);
				$.ajax({
					url:"/admin/addSubject",
					method:"POST",
					contentType:"application/json",
					xhrFields:{
						withCredentials: true
					},
					headers: { [window.csrfHeader]: window.csrfToken },
					data:JSON.stringify(subjectData),
					success:function(response){
						console.log("Success: ",response);
						toastr.success(response);
						$("#addsubjectForm").trigger("reset");
					},
					error:function(xhr,status,error){
						if(xhr.responseText.indexOf("already exists")!==-1){
							//toastr.warning("Subject already exists");
							var errorMessage=xhr.responseText;
							if(errorMessage.startsWith("Error:")){
									errorMessage=errorMessage.substring("Error:".length).trim();
							}
							toastr.warning(errorMessage);
							}else{
									toastr.error(xhr.responseText||"An unexpected error occur");
									console.log('Error: ' + status + ' - ' + error);
									console.log('Response: ' + xhr.responseText);
							}
						}
					});		
            });
            function creatingId(){
                var subjectName=$("#subjectName").val().trim();
                var subjectType=$("#subjectType").val();
                const words=subjectName.split(' ');
                const num=Math.floor(Math.random()*100);
                let initial='';
                let id='';
                if(words.length===1){
                    initial=words[0].slice(0,3).toUpperCase();
                    id=subjectType+initial+num;
                    console.log(words[0].slice(0,3))
                   
                }else{
                    words.forEach(element => {
                        if(element.toLowerCase() !=='of'){
                            initial=initial+element.charAt(0).toUpperCase();
                            id=subjectType+initial+num;
                        }
                    });
                }
                $("#subjectId").val(id);
            }
            $("#subjectName,#subjectType").on('input change',function(){
                creatingId();
            })
        });