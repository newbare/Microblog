$(document).ready(function () {
    // These functions make the file input buttons look bootstrap way
    $('input[type=file]').bootstrapFileInput();
    $('.file-inputs').bootstrapFileInput();

    // This function makes upload button enabled only when the file is chosen
    $('input:file').change(
            function(){
                if ($(this).val()) {
                    $('#uploadButton').prop('disabled',false);
                }
            }
        );
});
