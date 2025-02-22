/**
 * 
 */
$(document).ready(function(){
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
                    $("#departmentName").addClass(".error-border");
                    if(!InvalidField)InvalidField=$("#departmentName");
                    isValid=false;
                }
                const subjectType=$("#subjectType").val();
                if(!departmentName){
                    $("#subjectTypeError").text("Subject Type must required").show();
                    $("#subjectType").addClass(".error-border");
                    if(!InvalidField)InvalidField=$("#subjectType");
                    isValid=false;
                }
                const semesterNumber=parseInt($("#semesterNumber").val());
                if(!semesterNumber){
                    $("#semesterNumberError").text("Semester number must required").show();
                    $("#semesterNumber").addClass(".error-border");
                    if(!InvalidField)InvalidField=$("#semesterNumber");
                    isValid=false;
                }
                const subjectId=$("#subjectId").val();
                if(!subjectId){
                    $("#subjectIdError").text("Subject ID must required").show();
                    $("#subjectId").addClass(".error-border");
                    if(!InvalidField)InvalidField=$("#subjectId");
                    isValid=false;
                }
                if(InvalidField){
                    InvalidField.focus();
                }
                return isValid;
            }
            $("#subjectName,#departmentName,#subjectType,#semesterNumber").keydown(function(){
                $(".error-message").hide();
                $("input,select").removeClass("error-border");
            });
            $("#addsubjectForm").submit(function(event){
                event.preventDefault();
                if(!validateForm()){
                    return;
                }
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