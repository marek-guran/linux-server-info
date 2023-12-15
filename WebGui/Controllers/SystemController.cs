using Linux_Server_Info.Models;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System.Diagnostics;
using System.IO;
using System.Text.Json;

namespace Linux_Server_Info.Controllers
{
    public class SystemController : Controller
    {
        private readonly ILogger<SystemController> _logger;
        private readonly IWebHostEnvironment _webHostEnvironment;

        public SystemController(ILogger<SystemController> logger, IWebHostEnvironment webHostEnvironment)
        {
            _logger = logger;
            _webHostEnvironment = webHostEnvironment;
        }

        public IActionResult Index()
        {
            var jsonFilePath = Path.Combine(_webHostEnvironment.WebRootPath, "api/system_info.json");

            using (var streamReader = new StreamReader(jsonFilePath))
            {
                var json = streamReader.ReadToEnd();
                var serverInfo = JsonSerializer.Deserialize<ServerInfoModel>(json);

                var distribution = serverInfo?.Os?.Distribution?.ToLower() ?? "";
                var cpu1 = serverInfo?.Cpu?.Hardware?.ToLower() ?? "";
                var cpu2 = serverInfo?.Cpu?.Brand?.ToLower() ?? "";
                var svgFile = "linux.svg";
                var cpuInfo = "cpu.svg";

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

                if (cpu1.Contains("amd") || cpu2.Contains("amd"))
                {
                    cpuInfo = "amd.svg";
                }
                else if (cpu1.Contains("intel") || cpu2.Contains("intel"))
                {
                    cpuInfo = "intel.svg";
                }
                else if (cpu1.Contains("bcm") || cpu1.Contains("broadcom") || cpu2.Contains("bcm") || cpu2.Contains("broadcom"))
                {
                    cpuInfo = "broadcom.svg";
                }

                ViewBag.SvgFile = svgFile;
                ViewBag.CpuInfo = cpuInfo;
                return View(serverInfo);
            }
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
