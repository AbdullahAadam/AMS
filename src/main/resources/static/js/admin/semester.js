/**
 * 
 */
$(document).ready(function(){
           function validateForm(){
               let isValid=true;
               let InvalidField=null;
               $(".error-message").hide();
               $("input,select").removeClass("error-border");
               const semesterNo=parseInt($("#semesterNo").val().trim());
               if(!semesterNo ||semesterNo <1 || semesterNo>12){
                   $("#semesterNoError").text("Please enter valid Semester Number(1-12)").show();
                   $("#semesterNo").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#semesterNo");
                   isValid=false;
               }
               const startMonth=parseInt($("#startMonth").val());
               if(!startMonth){
                   $("#startMonthError").text("Start Month must required for Semester").show();
                   $("#startMonth").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#startMonth");
                   isValid=false;
               }
               const endMonth=parseInt($("#endMonth").val());
               if(!endMonth){
                   $("#endMonthError").text("End Month must required for Semester").show();
                   $("#endMonth").addClass("error-border");
                   if(!InvalidField)InvalidField=$("#endMonth");
                   isValid=false;
               }
               // if(endMonth<=startMonth){
               //     $("#endMonthError").text("End Month must be later than starting month").show();
               //     $("#endMonth").addClass("error-border");
               //     if(!InvalidField)InvalidField=$("#endMonth");
               //     isValid=false;
               // }
               if(semesterNo%2!=0){
                   if(startMonth==6 || startMonth==7){
                       if(endMonth!==12 && endMonth!==1){
                           $("#endMonthError").text("For odd Semester must be end with Decemeber or January").show();
                           $("#endMonth").addClass("error-border");
                           if(!InvalidField)InvalidField=$("#endMonth");
                           isValid=false;
                           return false;
                       }
                   }else{
                       $("#startMonthError").text("For odd Semester must be start with June or July").show();
                       $("#startMonth").addClass("error-border");
                       if(!InvalidField)InvalidField=$("#startMonth");
                       isValid=false;
                   }
               }else{
                   if(startMonth==12 || startMonth==1){
                       if(endMonth!==6 && endMonth!==5){
                           $("#endMonthError").text("For even Semester must be end with May or June ").show();
                           $("#endMonth").addClass("error-border");
                           if(!InvalidField)InvalidField=$("#endMonth");
                           isValid=false;
                       }
                   }else{
                       $("#startMonthError").text("For even Semester must be start with December or January ").show();
                       $("#startMonth").addClass("error-border");
                       if(!InvalidField)InvalidField=$("#startMonth");
                       isValid=false;
                   }
               }
               if(InvalidField){
                   InvalidField.focus();
               }
               return isValid;
           }
           $("#semesterNo").on("input",function(){
               $(".error-message").hide();
               $("input").removeClass("error-border");
           });
           $("#addSemesterForm").submit(function(event){
               event.preventDefault();
               if(!validateForm()){
                   return;
               }
           });

       });