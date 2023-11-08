package lotto.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lotto.StringConstants;
import lotto.domain.Lotto;
import lotto.port.InputPort;
import lotto.port.OutputPort;
import lotto.service.LottoNumberGenerationService;
import lotto.service.LottoPurchaseService;
import lotto.service.LottoResultCalculationService;
import lotto.service.LottoWinningNumberService;


public class LottoGameController {

    private final InputPort inputPort;
    private final OutputPort outputPort;
    private final LottoNumberGenerationService lottoNumberGenerationService;
    private final LottoPurchaseService lottoPurchaseService;
    private final LottoResultCalculationService lottoResultCalculationService;
    private final LottoWinningNumberService lottoWinningNumberService;


    public LottoGameController(InputPort inputPort, OutputPort outputPort, LottoNumberGenerationService lottoNumberGenerationService,
            LottoPurchaseService lottoPurchaseService, LottoResultCalculationService lottoResultCalculationService, LottoWinningNumberService lottoWinningNumberService) {

        this.inputPort = inputPort;
        this.outputPort =outputPort;
        this.lottoNumberGenerationService = lottoNumberGenerationService;
        this.lottoPurchaseService = lottoPurchaseService;
        this.lottoResultCalculationService = lottoResultCalculationService;
        this.lottoWinningNumberService = lottoWinningNumberService;
    }

    public void playGame() {
        List<Lotto> userLottoNumbers = buyLottoTickets();
        Lotto winningNumbers = inputWinningNumber();
        int bonusNumber = inputBonusNumber(winningNumbers);
        Map<Integer, Integer> matchingCounts = lottoResultCalculationService.calculateMatchingCounts(userLottoNumbers, winningNumbers, bonusNumber);
        lottoResultCalculationService.printStatistics(matchingCounts);
        lottoResultCalculationService.printReturnRate(matchingCounts, userLottoNumbers.size());
    }

    private List<Lotto> buyLottoTickets() {
        return getInputWithValidation(
                () -> {
                    outputPort.printLine(StringConstants.INPUT_PURCHASEAMOUNT_MESSAGE);
                    String purchaseAmount = inputPort.readLine();
                    outputPort.printEmptyLine();
                    int lottoTicketsCount = lottoPurchaseService.calculateNumberOfLottoTickets(purchaseAmount);
                    return lottoNumberGenerationService.generateLottoNumbers(lottoTicketsCount);
                }
        );
    }

    private Lotto inputWinningNumber() {
        return getInputWithValidation(
                () -> {
                    outputPort.printEmptyLine();
                    outputPort.printLine(StringConstants.INPUT_WINNING_NUMBER_MESSAGE);
                    String winningNumbers = inputPort.readLine();
                    return lottoWinningNumberService.addLottoNumberToWinningNumbers(winningNumbers);
                }
        );
    }

    private int inputBonusNumber(Lotto winningNumbers) {
        return getInputWithValidation(
                () -> {
                    outputPort.printEmptyLine();
                    outputPort.printLine(StringConstants.INPUT_BONUS_NUMBER_MESSAGE);
                    String bonusNumber = inputPort.readLine();
                    lottoWinningNumberService.validateBonusNumber(winningNumbers, bonusNumber);
                    return Integer.parseInt(bonusNumber);
                }
        );
    }

    private <T> T getInputWithValidation(Supplier<T> inputSupplier) {
        while (true) {
            try {
                return inputSupplier.get();
            } catch (IllegalArgumentException e) {
                outputPort.printLine(e.getMessage());
            }
        }
    }
}
