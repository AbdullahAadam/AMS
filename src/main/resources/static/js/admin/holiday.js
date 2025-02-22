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
            var today=new Date().toISOString().split('T')[0];
            $("#holidayDate").attr('min',today);
            function validateForm(){
                let isValid=true;
                let InvalidField=null;
                $(".error-message").hide();
                $("input,select").removeClass("error-border");
                const holidayName=$("#holidayName").val().trim();
                if(!holidayName){
                    $("#holidayNameError").text("Holiday Name must required").show();
                    $("#holidayName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#holidayName");
                    isValid=false;
                }else if(holidayName.length < 4 || holidayName.length>50){
                    $("#holidayNameError").text("Holiday name must between 4 to 50 characters").show();
                    $("#holidayName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#holidayName");
                    isValid=false;
                }
                const holidayDate=$("#holidayDate").val();
                if(!holidayDate){
                    $("#holidayDateError").text("Holiday Date must required").show();
                    $("#holidayDate").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#holidayDate");
                    isValid=false;
                }
                const holidayReason=$("#holidayReason").val().trim();
                if(!holidayReason){
                    $("#holidayReasonError").text("Holiday Reason must required").show();
                    $("#holidayReason").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#holidayReason");
                    isValid=false;
                }
                if(InvalidField){
                    InvalidField.focus();
                }
                return isValid;
            }
            $("#holidayName,#holidayDate,#holidayReason").on("input",function(){
                $(".error-message").hide();
                $("input").removeClass("error-border");
            });
            $("#addHolidayForm").submit(function(event){
                event.preventDefault();
                if(!validateForm()){
                    return;
                }
				var holidayData={
					holidayName:$("#holidayName").val().trim(),
					holidayDate:$("#holidayDate").val(),
					holidayType:$("#holidayReason").val().trim()
					
				};
				console.log("HolidayDatas: ",holidayData);
				console.log("CSRF Token:", csrfToken);
				console.log("CSRF Header:", csrfHeader);
				var csrfToken = $("meta[name='_csrf']").attr("content");
				var csrfHeader = $("meta[name='_csrf_header']").attr("content");
				$.ajax({
					url:"/admin/addHoliday",
					method:"POST",
					contentType:"application/json",
					xhrFields: {
					       withCredentials: true  // Ensure cookies are sent
					},
					headers: { [window.csrfHeader]: window.csrfToken },
					data:JSON.stringify(holidayData),
					success:function(response){
						console.log("Success: ",response);
						toastr.success(response);

					},
					error:function(xhr,status,error){
						if(xhr.responseText.indexOf("already exists")!==-1){
							toastr.warning("A Holiday with this date already exists");
						}else{
							//console.error("Error:", xhr.responseText);
							toastr.error(xhr.responseText ||"An unexpected error occured");
						}
					}
				});
            });
        });