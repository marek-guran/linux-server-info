using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Hosting;
using System.Threading.Tasks;
using System.IO;

namespace Linux_Server_Info.Controllers
{
    [Route("api")]
    [ApiController]
    public class ApiController : ControllerBase
    {
        private readonly IWebHostEnvironment _env;

        public ApiController(IWebHostEnvironment env)
        {
            _env = env;
        }

        [HttpGet]
        public async Task<IActionResult> GetSystemInfo()
        {
            var path = Path.Combine(_env.WebRootPath, "api", "system_info.json");

            if (!System.IO.File.Exists(path))
            {
                return NotFound("JSON file not found.");
            }

            var jsonContent = await System.IO.File.ReadAllTextAsync(path);
            return Content(jsonContent, "application/json");
        }
    }
}
