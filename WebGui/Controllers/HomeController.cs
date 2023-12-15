using Linux_Server_Info.Models;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System.Diagnostics;
using System.IO;
using System.Text.Json;

namespace Linux_Server_Info.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private readonly IWebHostEnvironment _webHostEnvironment;

        public HomeController(ILogger<HomeController> logger, IWebHostEnvironment webHostEnvironment) // Inject IWebHostEnvironment
        {
            _logger = logger;
            _webHostEnvironment = webHostEnvironment; // Assign the injected web host environment to the field
        }

        public IActionResult Index()
        {
            var jsonFilePath = Path.Combine(_webHostEnvironment.WebRootPath, "api/system_info.json");

            using (var streamReader = new StreamReader(jsonFilePath))
            {
                var json = streamReader.ReadToEnd();
                var serverInfo = JsonSerializer.Deserialize<ServerInfoModel>(json);

                var distribution = serverInfo?.Os?.Distribution?.ToLower() ?? "";
                var svgFile = "linux.svg"; // default

                if (distribution.Contains("ubuntu"))
                {
                    svgFile = "ubuntu.svg";
                }
                else if (distribution.Contains("raspbian") || distribution.Contains("raspberry"))
                {
                    svgFile = "raspberry.svg";
                }
                else if (distribution.Contains("debian"))
                {
                    svgFile = "debian.svg";
                }

                ViewBag.SvgFile = svgFile;
                return View(serverInfo);
            }
        }



        public IActionResult Privacy()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}