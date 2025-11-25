$(document).ready(function () {
      // Auto Age Calculation
      $('#dob').on('change', function () {
        const dob = new Date(this.value);
        const today = new Date();
        let age = today.getFullYear() - dob.getFullYear();
        const m = today.getMonth() - dob.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
          age--;
        }
        $('#age').val(age);
      });
	  $.ajax({
	          url: '/listDepartments',
	          type: 'GET',
	          dataType: 'json',
	          success: function (data) {
	  			console.log(data);
	              if (data.length === 0) {
	                  $("#department-error").text("No departments available.").show();
	              } else {
	                  $.each(data, function (index, department) {
	                      $('#department').append(
	                          $('<option>', {
	                              value: department.deptId,
	                              text: department.deptName,
	                              id: department.code
	                          })
	                      );
	                  });
	              }
	          },
	          error: function (xhr, status, error) {
	              console.log("Error fetching departments:", error);
	          }
	      });
		  $("#department").change(function () {
		          var deptId = $(this).val();
		          $("#mentor").empty().append('<option value="">Select Mentor</option>');

		          if (deptId) {
		              $.ajax({
		                  url: "/professors/" + deptId,
		                  type: "GET",
		                  success: function (data) {
		                      if (data.length === 0) {
		                          $("#mentor-error").text("No professors available for this department.").show();
		                      } else {
		                          $.each(data, function (index, professor) {
		                              $("#mentor").append('<option value="' + professor.profId + '">' + professor.name + '</option>');
		                          });

		                          // Auto-select the only professor if only one is available
		                          if (data.length === 1) {
		                              $("#mentor").val(data[0].profId);
		                          }

		                          $("#mentor-error").hide();
		                      }
		                  },
		                  error: function (xhr, status, error) {
		                      console.log("Error fetching professors:", error);
		                  }
		              });
		          }
		      });

      // Form validation
      $('#registrationForm').on('submit', function (e) {
        let isValid = true;

        // Clear previous errors
        $('.error-msg').text('');
        $('input, select, textarea').removeClass('error-field');

        const requiredFields = [
          { name: 'regNo', label: 'Reg No' },
          { name: 'name', label: 'Name' },
          { name: 'gender', label: 'Gender' },
          { name: 'dob', label: 'DOB' },
          { name: 'mobile', label: 'Mobile Number' },
          { name: 'address', label: 'Address' },
          { name: 'department', label: 'Department' },
          { name: 'batch', label: 'Batch' },
          { name: 'mentor', label: 'Mentor' },
          { name: 'email', label: 'Email' },
          { name: 'password', label: 'Password' },
          { name: 'confirmPassword', label: 'Confirm Password' }
        ];

        requiredFields.forEach(field => {
          const el = $('[name="' + field.name + '"]');
          const value = el.val();
          if (!value || value === '') {
            $('#' + field.name + '-error').text(field.label + ' is required');
            el.addClass('error-field');
            isValid = false;
          }
        });

        const pass = $('#password').val();
        const confirmPass = $('#confirmPassword').val();
        if (pass !== confirmPass) {
          $('#confirmPass-error').text('Passwords do not match');
          $('#confirmPassword').addClass('error-field');
          isValid = false;
        }

        if (!isValid) {
          e.preventDefault();
        }
      });

      // Clear errors on typing/input
      $('input, select, textarea').on('input change', function () {
        const name = $(this).attr('name');
        $(this).removeClass('error-field');
        $('#' + name + '-error').text('');
      });
    });