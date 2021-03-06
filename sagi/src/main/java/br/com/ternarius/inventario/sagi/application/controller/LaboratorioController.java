package br.com.ternarius.inventario.sagi.application.controller;

import br.com.ternarius.inventario.sagi.application.dto.LaboratorioDto;
import br.com.ternarius.inventario.sagi.domain.enums.Edificio;
import br.com.ternarius.inventario.sagi.domain.enums.Message;
import br.com.ternarius.inventario.sagi.domain.repository.LaboratorioRepository;
import br.com.ternarius.inventario.sagi.domain.repository.UsuarioRepository;
import br.com.ternarius.inventario.sagi.domain.service.LaboratorioService;
import br.com.ternarius.inventario.sagi.util.ConverterEntitiesToDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

/**
 * 
 * @author Elvis da Guarda
 *
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/laboratorio")
public class LaboratorioController extends BaseController {

	private final LaboratorioService service;
	private final LaboratorioRepository laboratorioRepository;
	private final UsuarioRepository usuarioRepository;
	
	@GetMapping
	public ModelAndView index(ModelAndView modelAndView, @AuthenticationPrincipal UserDetails userDetails) {
		if (!IsLogged()) {
			modelAndView.setViewName("redirect:/login");
			return modelAndView;
		}
		
		userLogged(modelAndView, userDetails, usuarioRepository);

		modelAndView.setViewName("laboratorio/index");
		modelAndView.addObject("dto", new LaboratorioDto());
		modelAndView.addObject("laboratoriosDto", new ConverterEntitiesToDtos().fromLaboratorios(service.findAll()));
		
		return modelAndView;
	}

	@GetMapping("/cadastro-laboratorio")
	@PreAuthorize("hasRole('ADMIN')")
	public ModelAndView create(ModelAndView modelAndView, @AuthenticationPrincipal UserDetails userDetails) {
		if (!IsLogged()) {
			modelAndView.setViewName("redirect:/login");
			return modelAndView;
		}
		
		userLogged(modelAndView, userDetails, usuarioRepository);

		modelAndView.addObject("dto", new LaboratorioDto());
		modelAndView.addObject("edificios", Edificio.values());
		modelAndView.setViewName("laboratorio/create");
		
		return modelAndView;
	}

	@Transactional
	@PostMapping("/cadastro-laboratorio")
	@PreAuthorize("hasRole('ADMIN')")
	public ModelAndView save(@Valid @ModelAttribute("dto") LaboratorioDto dto, BindingResult result,
			RedirectAttributes attributes, ModelAndView modelAndView) {
		if (hasErrors(result, modelAndView)) {
			attributes.addFlashAttribute("success", false);

			modelAndView.setViewName("laboratorio/create");
			modelAndView.addObject("dto", dto);
			modelAndView.addObject("edificios", Edificio.values());

			return modelAndView;
		}
		
		service.cadastrar(dto.toEntity());
		
		modelAndView.setViewName("redirect:/laboratorio/cadastro-laboratorio");
		attributes.addFlashAttribute("msg", Message.MSG_05.getMessage());
		
		return modelAndView;
	}

	@GetMapping("/editar-laboratorio/{idLaboratorio}")
	@PreAuthorize("hasRole('ADMIN')")
	public ModelAndView edit(@PathVariable("idLaboratorio") Optional<String> record, ModelAndView modelAndView) {
		if (!IsLogged()) {
			modelAndView.setViewName("redirect:/login");
			return modelAndView;
		}
		
		var actionName = "redirect:/laboratorio";
		
		if (!hasRecord(record, laboratorioRepository, modelAndView, actionName)) {
			return modelAndView;
		}
		
		var lab = laboratorioRepository.findById(record.get()).get();
		var dto = new LaboratorioDto(lab);

		modelAndView.addObject("dto", dto);
		modelAndView.addObject("edificios", Edificio.values());

		modelAndView.setViewName("laboratorio/edit");
		
		return modelAndView;
	}

	@Transactional
	@PostMapping("/editar-laboratorio")
	@PreAuthorize("hasRole('ADMIN')")
	public ModelAndView edit(@Valid @ModelAttribute("dto") LaboratorioDto dto, BindingResult result,
			ModelAndView modelAndView, RedirectAttributes attributes) {
		if (hasErrors(result, attributes)) {
			modelAndView.addObject("dto", dto);
			modelAndView.setViewName("redirect:/laboratorio/editar-laboratorio/" + dto.getId());
			
			return modelAndView;
		}
		
		service.update(dto.toEntity());

		attributes.addFlashAttribute("success", true);
		modelAndView.setViewName("redirect:/laboratorio");
		
		return modelAndView;
	}
	
	@Transactional
	@PostMapping("/deletar-equipamento")
	@PreAuthorize("hasRole('ADMIN')")
	public ModelAndView delete(@ModelAttribute("dto") LaboratorioDto dto, ModelAndView modelAndView, RedirectAttributes attributes) {
		return modelAndView;
	}
}